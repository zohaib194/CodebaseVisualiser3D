var data;

var color_lightgray = 0x808080;
var color_white = 0xffffff;
var color_green = 0x00ff00;

// Find canvas.
var canvas = document.getElementById("output");

// Make as setup renderer for rendering on canvas. 
var renderer = new THREE.WebGLRenderer({ canvas: canvas });
renderer.setSize(window.innerWidth, window.innerHeight);
renderer.setClearColor(0xffffff, 1);

// Make scene to render.
const scene = new THREE.Scene();

// Setup camera.
var camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 0.1, 1000);
camera.position.set(0, 0, 5);

// Add controls on camera and update to apply camera position change.
var controls = new THREE.OrbitControls(camera, renderer.domElement);
controls.update();

// Make lights.
var light = new THREE.AmbientLight(color_lightgray); // Soft white light
scene.add(light);

var light = new THREE.PointLight(color_white, 1, 100);
light.position.set(10, 10, 10);
scene.add(light);

// Add resize listener to resize renderer and camera.
window.addEventListener("resize", function () {
    renderer.setSize(window.innerWidth, window.innerHeight);
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
});

// Cube marking origin of world.
var geometry = new THREE.CubeGeometry(0.05, 0.05, 0.05);
var material = new THREE.MeshBasicMaterial({ color: 0x00ff00 });
var cube = new THREE.Mesh(geometry, material);
cube.position.set(0, 0, 0);
scene.add(cube);

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
function mainloop() {
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
    for (i = 0; i < fdg.nodes.length; i++) {
        for (j = 0; j < fdg.nodes.length; j++) {
            fdg.addLink(i, j, new LinkProperties(-1));
        }
    }

    // Run for 100 iterations shifting the position of nodes.
    fdg.execute(100);

    // Draw nodes using display manager.
    fdg.nodes.forEach((node) => {
        var supportedType = false;
        var shapeGeometry;

        // Select shape based on node type.
        if (node.type == "function") {
            shapeGeometry = new THREE.BoxGeometry(0.1, 0.1, 0.1);
            supportedType = true;
        } else if (node.type == "class") {
            shapeGeometry = new THREE.CylinderGeometry(0.05, 0.05, 0.1, 32);
            supportedType = true;
        } else if (node.type == "namespace") {
            shapeGeometry = new THREE.ConeGeometry(0.05, 0.1, 32);
            supportedType = true;
        } else {    // Unsupported node type, mention this!
            console.log("Unsupported type! " + node.type);
        }
        
        // Foudn a supported type.
        if (supportedType) {
            // Add it for display.
            displayMgr.addObject(
                "function",
                new DisplayObject(
                    node.position,
                    color_green,
                    node.name,
                    new THREE.Mesh(
                        shapeGeometry,
                        new THREE.MeshStandardMaterial({ color: 0x00ff00 })
                    )
                )
            );
        }
        
        // Draw nodes links with three.js
        node.links.forEach((link, otherIndex) => {
            if (link.attraction > 0) {
                var material = new THREE.LineBasicMaterial({
                    color: 0x0000ff
                });
                
                var geometry = new THREE.Geometry();
                geometry.vertices.push(
                    node.position,
                    fdg.nodes[otherIndex].position
                );
                
                var line = new THREE.Line(geometry, material);
                scene.add(line);
            }
        });
    });
}

// Find id param form url
var id = new URL(window.location.href).searchParams.get("id");

// Create a http request
var xhr = new XMLHttpRequest();

// Open the connection
xhr.open("get", "http://localhost:8080/repo/" + id, true);

// Once ready, receive data and populate displaymanager.
xhr.onreadystatechange = function () {
    console.log("Got the data from backend!");
    // Once ready and everything went ok.
    if (xhr.readyState == 4 && xhr.status == 200) {
        data = JSON.parse(xhr.responseText);

        // Didn't get data, abort.
        if (typeof data === "undefined" || data == null) {
            return;
        }

        // Parse data and perform fdg.
        runFDGOnJSONData(data);
    }
}
xhr.send();

// Start program loop.
mainloop();