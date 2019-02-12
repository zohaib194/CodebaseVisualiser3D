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
displayMgr.addObject("function", new DisplayObject(new THREE.Vector3(0, 0, 0), color_green, "display1"));
displayMgr.addObject("function", new DisplayObject(new THREE.Vector3(2, 0, 0), color_green, "display2"));
displayMgr.addObject("function", new DisplayObject(new THREE.Vector3(0, 2, 0), color_green, "display3"));
displayMgr.addObject("function", new DisplayObject(new THREE.Vector3(0, 0, 2), color_green, "display4"));

/**
 * Program lifecycle function reponsible for updating the program state.
 */
function update()
{
    // Update controls.
    controls.update();
}

/**
 * Program lifecycle function reponsible for rendering the scene.
 */
function render()
{
    // Display all functions.
    displayMgr.draw(scene);

    // Draw scene.
    renderer.render(scene, camera);
}

/**
 * Main loop of program.
 */
function mainloop()
{
    // Schedule the next frame.
    requestAnimationFrame(mainloop);

    update();
    render();
}

// Start program loop.
mainloop();