var data;

var color_lightgray = 0x808080;
var color_white = 0xffffff;
var color_green = 0x00ff00;

// Find canvas.
var canvas = document.getElementById("output");

// Make as setup renderer for rendering on canvas. 
var renderer = new THREE.WebGLRenderer({ canvas: canvas });
renderer.setSize(window.innerWidth, window.innerHeight);
renderer.setClearColor( 0xffffff, 1);

// Make scene to render.
const scene = new THREE.Scene();

// Setup camera.
var camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
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
window.addEventListener("resize", function() {
    renderer.setSize(window.innerWidth,window.innerHeight);
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
});

// Add displaymanager for managing objects to draw.
var displayMgr = new DisplayManager();

// Cube marking origin of world.
var geometry = new THREE.CubeGeometry(0.05, 0.05, 0.05);
var material = new THREE.MeshBasicMaterial({color: 0x00ff00});
var cube = new THREE.Mesh(geometry, material);
cube.position.set(0, 0, 0);
scene.add(cube);

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
 * Function for displaying function data fron json object.
 * @param {object} data - Object made from Json.parse().
 */
function displayFunctions(data) {
    if (typeof data.functions === "undefined" || data.functions.lenght <= 0)
        return;
    
    // For every functions entry
    data.functions.forEach(element => {
        // For each function object
        element.function_names.forEach((func) => {
            // Add it for display.
            displayMgr.addObject(
                "function", 
                new DisplayObject(
                    new THREE.Vector3(
                        // Random nr [0-9].
                        Math.floor(Math.random() * 10), 
                        Math.floor(Math.random() * 10), 
                        Math.floor(Math.random() * 10)
                    ), 
                    color_green, 
                    func.name
                )
            );
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
xhr.onreadystatechange = function() {
    // Once ready and everything went ok.
    if(xhr.readyState == 4 && xhr.status == 200) {
        data = JSON.parse(xhr.responseText);

        // Didn't get data.
        if (typeof data === "undefined") {
            return;
        }

        displayFunctions(data);
    }
}
xhr.send();

// Start program loop.
mainloop();