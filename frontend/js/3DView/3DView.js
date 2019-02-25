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
    // Schedule the next frame.
    requestAnimationFrame(mainloop);

    update();
    render();
}

// ########## DATA PROCESSING FUNCITONS ##########

/**
* Function for processing json data and building FDG.
* @param {object} data - Object made from Json.parse().
*/
function runFDGOnJSONData(data) {
    // Build fdg graph from parsed json data.
    handleProjectData(data);

    // Apply negative links on those who aren't related
    for (i = 0; i < fdg.getNodes().length; i++) {
        for (j = 0; j < fdg.getNodes().length; j++) {
            fdg.addLink(i, j, new LinkProperties(-1));
        }
    }

    // Run for 100 iterations shifting the position of nodes.
    fdg.execute(100);

    // Draw nodes using display manager.
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

// Find id param form url
var id = new URL(window.location.href).searchParams.get("id");

fetch("http://" + config.serverInfo.api_ip + ":" + config.serverInfo.api_port + "/repo/" + id)
.then((response) => {
    // Once ready and everything went ok.
    if (response.status == 200) {
        console.log("Got something, moving on!");
        return response.json();
    }

    console.log("Didn't receive anything!");
    // Something went wrong.
    return Promise.reject();
}).then((json) => {
    var sentence = LOCALE.getSentence("backend_data_received");
    console.log(sentence);
    
    // Didn't get data, abort.
    if (typeof json === "undefined" || json == null) {
        // Json missing or unparsable.
        return Promise.reject();
    }
    
    console.log("Got valid data!");

    // Parse data and perform fdg.
    runFDGOnJSONData(json);
    
    // Disable the loader icon.
    document.getElementById("loader").style.display = "none";
    
    // Start program loop.
    requestAnimationFrame(mainloop);
}).catch((error) => {
    console.log("Error: " + error);
});