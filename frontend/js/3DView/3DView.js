// Find canvas.
var canvas = document.getElementById("output");

// Make as setup renderer for rendering on canvas. 
var renderer = new THREE.WebGLRenderer({ canvas: canvas });
renderer.setSize(window.innerWidth, window.innerHeight);
renderer.setClearColor(STYLE.getColors().background, 1);

// Make scene to render.
const scene = new THREE.Scene();

// Setup camera.
var camera = new THREE.PerspectiveCamera(
    STYLE.getGraphics().camera.fov, 
    window.innerWidth / window.innerHeight, 
    STYLE.getGraphics().camera.nearPlane,
    STYLE.getGraphics().camera.farPlane,
);
camera.position.set(0, 0, 5);

// Add controls on camera and update to apply camera position change.
var controls = new THREE.OrbitControls(camera, renderer.domElement);
controls.update();

// Make lights.
var light = new THREE.AmbientLight(
    STYLE.getColors().ambient
);
scene.add(light);

var directionalLight = new THREE.DirectionalLight(
    STYLE.getColors().light, 
    0.5
);
directionalLight.position.set(1, 1, 1);
scene.add(directionalLight);

// Add resize listener to resize renderer and camera.
window.addEventListener("resize", function () {
    renderer.setSize(window.innerWidth, window.innerHeight);
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
});

// Draw to remove black screen.
renderer.render(scene, camera);

// Cube marking origin of world.
var originGeometry = new THREE.CubeGeometry(0.05, 0.05, 0.05);
var originMaterial = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
var originCube = new THREE.Mesh(originGeometry, originMaterial);
originCube.position.set(0, 0, 0);
scene.add(originCube);

// Add displaymanager for managing objects to draw.
var displayMgr = new DisplayManager();

// Force-Directed-Graph for managing object grouping.
var fdg = new FDG(1, 1, 0.1, new THREE.Vector3(0, 0, 0));

// Window manager
var windowMgr = new WindowManager();

// Mouse event variables
var raycaster = new THREE.Raycaster();
var mouse = new THREE.Vector2();

/**
 * Program lifecycle function reponsible for updating the program state.
 */
function update() {
    // Update controls.
    controls.update();
}

/**
 * Program lifecycle function reponsible for rendering the scene.
 */
function render() {
    // Display all functions.
    displayMgr.draw();

    // Draw scene.
    renderer.render(scene, camera);
}

/**
 * Main loop of program.
 */
function mainloop(time) {

    update();
    windowMgr.ImGuiUpdate(time);

    render();
    windowMgr.ImGuiRender();
    
    // Schedule the next frame.
    requestAnimationFrame(mainloop);
}

// ########## DATA PROCESSING FUNCITONS ##########

/**
* Function for processing json data and building FDG.
* @param {object} data - Object made from Json.parse().
*/
function runFDGOnJSONData(data) {
    // Build fdg graph from parsed json data.
    document.getElementById("status").innerHTML = 
        LOCALE.getSentence("userinfo_read");
    handleProjectData(data);

    // Apply negative links on those who aren't related
    for (i = 0; i < fdg.getNodes().length; i++) {
        for (j = 0; j < fdg.getNodes().length; j++) {
            fdg.addLink(i, j, new LinkProperties(-1));
        }
    }

    // Run for 100 iterations shifting the position of nodes.
    document.getElementById("status").innerHTML = 
        LOCALE.getSentence("userinfo_organization");
    fdg.execute(100);

    // Draw nodes using display manager.
    document.getElementById("status").innerHTML = 
        LOCALE.getSentence("userinfo_structure_visualization_assigment");
    fdg.getNodes().forEach((node) => {
        /**
         * Function for selecting proper type from configuration.
         * @param {string} type - The type of node.
         */
        let getDrawableGeometry = (function(type) {
            switch(type) {
                case "cube":
                    return new THREE.BoxGeometry(0.1, 0.1, 0.1);
                case "sphere":
                    return new THREE.SphereGeometry(0.1, 32, 16);
                case "cylinder":
                    return new THREE.CylinderGeometry(0.05, 0.05, 0.1, 16);
                case "cone":
                    return new THREE.ConeGeometry(0.05, 0.1, 16);
                case "dodecahedron":
                    return new THREE.DodecahedronGeometry(0.05);
                case "icosahedron":
                    return new THREE.IcosahedronGeometry(0.05);
                case "octahedron":
                    return new THREE.OctahedronGeometry(0.05);
                case "tetrahedron":
                    return new THREE.TetrahedronGeometry(0.05);
            }
        });

        var nodeType = node.getType();
        var supportedType = false;
        var drawableGeometry;
        var drawableColor;

        // Select shape and color based on node type.
        switch (nodeType) {
            case "function": {
                drawableColor = STYLE.getDrawables().function.color;
                drawableGeometry = getDrawableGeometry(
                    STYLE.getDrawables().function.shape
                );
                supportedType = true;
                break;
            }
            case "class": {
                drawableGeometry = getDrawableGeometry(
                    STYLE.getDrawables().class.shape
                );
                drawableColor = STYLE.getDrawables().class.color;
                supportedType = true;
                break;
            }
            case "namespace": {
                drawableGeometry = getDrawableGeometry(
                    STYLE.getDrawables().namespace.shape
                );
                drawableColor = STYLE.getDrawables().namespace.color;
                supportedType = true;
                break;
            }
            default: {   // Unsupported node type, mention this!
                console.log(
                    LOCALE.getSentence("geometry_invalid_type") + ": " + node.getType()
                );
                break;
            }
        }
        
        // Found a supported type.
        document.getElementById("status").innerHTML = 
            LOCALE.getSentence("userinfo_ready_display");
        if (supportedType) {
            // Add it for display.
            displayMgr.addObject(
                node.getType(),
                new Drawable(
                    node.getPosition(),
                    drawableColor,
                    node.getName(),
                    drawableGeometry
                )
            );

            // Draw nodes links with three.js
            node.getLinks().forEach((link, otherIndex) => {
                if (link.attraction > 0) {
                    var material = new THREE.LineBasicMaterial({
                        color: STYLE.getDrawables().link.color
                    });
                    
                    var geometry = new THREE.Geometry();
                    geometry.vertices.push(
                        node.getPosition(),
                        fdg.getNodes()[otherIndex].getPosition()
                    );
                    
                    var line = new THREE.Line(geometry, material);
                    scene.add(line);
                }
            });
        }
    });
}

// ########## Mouse events functions ##########

/**
 * On click mouse event function.
 *
 * @param      {Event}  event   The event.
 */
function onMouseClick(event) {

    // calculate mouse position in normalized device coordinates
    // (-1 to +1) for both components
    mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
    mouse.y = - (event.clientY / window.innerHeight) * 2 + 1;

    // update the picking ray with the camera and mouse position
    raycaster.setFromCamera(mouse, camera);

    // calculate objects intersecting the picking ray
    var intersects = raycaster.intersectObjects(scene.children ,true);

    if(intersects !== "undefined" && intersects.length > 0) {
        var funcName = intersects[0].object.name.substr(0, intersects[0].object.name.indexOf(' |'));

        sendGetRequest("http://" + config.serverInfo.api_ip + ":" + config.serverInfo.api_port + 
            "/repo/" + id + "/file/read/?lineStart=" + functionModels.get(funcName).getStartLine() + 
            "&lineEnd=" + functionModels.get(funcName).getEndLine() +
            "&filePath=" + functionModels.get(funcName).getFileName())
        .then(json => {
            windowMgr.setDataStructureImplementation(json.implementation); 
        });
    }
}

window.addEventListener('mousedown', onMouseClick);

// Find reponame param form url
var repoName = new URL(window.location.href).searchParams.get("repo");

// id of the repo sumbitted to back-end.
var id = 0;

/**
 * Sends a get request to given url.
 *
 * @param      {string}  url     The url
 * @return     {Promise}  A promise containing json from the response.
 */
function sendGetRequest(url){
    return fetch(url)
            .then((response) => {
                // Once ready and everything went ok.
                if (response.status == 200) {
                    return response.json();
                }

                // Something went wrong.
                console.log(LOCALE.getSentence("backend_data_not_received"));
                return Promise.reject();
            }).then((json) => {
                console.log(LOCALE.getSentence("backend_data_received"));

                // Didn't get data, abort.
                if (typeof json === "undefined" || json == null) {
                    // Json missing or unparsable.
                    return Promise.reject();
                }
                return json;
            }).catch(error => console.log(error));
}

// Request to get the list of repositories stored in DB.
sendGetRequest("http://" + config.serverInfo.api_ip + ":" + config.serverInfo.api_port + "/repo/list")
.then(json => {
   windowMgr.setRepositories(json); 
});

/**
 * Sends an get request for the repository data and update feedback status through websocket.
 */
function sendInitialRequest() {
    // Websocket connection for the api endpoint.
    var websocket = new WebSocket("ws://" + config.serverInfo.api_ip + ":" + config.serverInfo.api_port + "/repo/"+id+"/initial/");

    // Message recieved from server.
    websocket.onmessage = function (event) {
        console.log(LOCALE.getSentence("userinfo_websocket_initial_message"));

        // Parse the server response.
        var response = JSON.parse(event.data)

        // set the id related to repository.
        id = response.body.id;

        // Everything went ok, display data.
        if (response.statuscode >= 400) {
            // Update status and exit.
            document.getElementById("status").innerHTML = 
                LOCALE.getSentence("userinfo_websocket_initial_message_failed") + 
                ": " + response.body.id;
            return;
        }

        switch (response.body.status) {
            // Still parsing. 
            case "Parsing": {
                // Display current file and file counts.
                document.getElementById("status").innerHTML = 
                    LOCALE.getSentence("userinfo_websocket_initial_message_status_parsing") + 
                    ": " + response.body.currentFile;
                if (typeof response.body.parsedFileCount !== "undefined") {
                    document.getElementById("status_parsedcount").innerHTML = 
                        LOCALE.getSentence("userinfo_websocket_initial_message_parsed") + 
                        ": " + response.body.parsedFileCount;
                }
                if (typeof response.body.skippedFileCount !== "undefined") {
                    document.getElementById("status_skippedcount").innerHTML = 
                        LOCALE.getSentence("userinfo_websocket_initial_message_skipped") + 
                        ": " + response.body.skippedFileCount;
                }

                break;
            }
            // Finished parsing.
            case "Done": {

                // Display finished message and final file count.
                if (typeof response.body.currentFile !== "undefined") {
                    document.getElementById("status").innerHTML = 
                    LOCALE.getSentence("userinfo_websocket_initial_message_status_finished") + 
                    ": " + response.body.currentFile;
                }

                if (typeof response.body.parsedFileCount !== "undefined") {
                    document.getElementById("status_parsedcount").innerHTML = 
                        LOCALE.getSentence("userinfo_websocket_initial_message_parsed") + 
                        ": " + response.body.parsedFileCount;
                }
                if (typeof response.body.skippedFileCount !== "undefined") {
                    document.getElementById("status_skippedcount").innerHTML = 
                        LOCALE.getSentence("userinfo_websocket_initial_message_skipped") + 
                        ": " + response.body.skippedFileCount;
                }
                parsedFileCount = response.body.parsedFileCount;
                skippedFileCount = response.body.skippedFileCount;

                // Continue with parsing.
                runFDGOnJSONData(response.body.result);
                break;
            }
        }
    };
    
    websocket.onclose = function (event) {
        // Disable the loader icon and status tags.
        document.getElementById("loader").style.display = "none";
        document.getElementById("status").style.display = "none";
        document.getElementById("status_parsedcount").style.display = "none";
        document.getElementById("status_skippedcount").style.display = "none";

        // Start three.js loop
        requestAnimationFrame(mainloop);
        
        // Closed websocket.
        websocket.close();
    }
};

/**
 * Sends an add request to submit the repository and update feedback status through websockets.
 */
function sendAddRequest(){ 
    // Websocket connection for the api endpoint.
    var websocket = new WebSocket("ws://" + config.serverInfo.api_ip + ":" + config.serverInfo.api_port + "/repo/add");

    // Once websocket connection is open send the request to add repository.
    websocket.onopen = function(){
        websocket.send(JSON.stringify({"uri": repoName}));
    }

    // Message recieved from server.
    websocket.onmessage = function (event) {
        // Parse the server response.
        var response = JSON.parse(event.data)

        
        if (response.statuscode == 202) {
            // set the id related to repository.
            id = response.body.id;
            
            // Update status.
            document.getElementById("status").innerHTML = response.body.status;
        }
    }

    websocket.onclose = function (event) {
        var reason = JSON.parse(event.reason)
        // if the repository already exist, set the id to existing repo.
        if(reason.statuscode == 409){
            document.getElementById("status").innerHTML = reason.body.status;
            id = reason.body.id;
        } else if (reason.statuscode == 400){
            document.getElementById("status").innerHTML = reason.body.status;
            location.assign("../index.html");
            return;
        }
        
        // Open websocket for initial request status data.
        sendInitialRequest();
        websocket.close();
    }
};
sendAddRequest();
