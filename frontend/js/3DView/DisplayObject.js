var nameplate_container = document.getElementById("nameplate_container");

/**
 * Private function for placing nameplate on 3d objects.
 * @param {string} name - Name/Id of nameplate.
 * @param {Integer} x_pos - X-axis of screen position.
 * @param {Integer} y_pos - Y-axis of screen position
 */
let placeNameplate = function (name, x_pos, y_pos) {
    // Try to find nameplate for object with name "name".
    var nameplate = document.getElementById(name);

    // Couldn't find it, create and set it up (boilerplate).
    if (nameplate == null) {
        nameplate = document.createElement("div");
        nameplate.textContent = name;
        nameplate.className = "nameplate";
        nameplate.id = name;
        nameplate.style = `
            position: absolute;
            z-index: 9999;
            text-align: center;
            display: block;
            color: #000000;
        `;
    }

    // Set position to be near the threejs object with name "name".
    // Using 'px' here cause it's the acutal screen position.
    nameplate.style.left = (x_pos + 50) + 'px';
    nameplate.style.top = (y_pos - 50) + 'px';

    // Add it.
    nameplate_container.appendChild(nameplate);
};

/**
 * Private function for removing/hiding nameplates.
 * @param {string} name - Name of nameplate to destroy.
 */
let destroyNameplate = function(name) {
    // Try to find nameplate.
    var nameplate = document.getElementById(name);
    if (nameplate != null)  // Delete if it exists.
        nameplate_container.removeChild(nameplate);
}

/**
 * A generic object used for representing functions and other syntax.
 * @constructor
 * 
 * @param {THREE.Vector3} position - Position the cube willl be placed at.
 * @param {Integer} color - The color of the cube, preferrably not black.
 * @param {string} name - Name of the obejct to be display on hover (not active).
 */
function DisplayObject(position, color, name) {
    this.position = position;
    this.color = color;
    this.name = name;

    // Cube setup.
    this.geometry = new THREE.BoxGeometry(1, 1, 1);
    this.material = new THREE.MeshStandardMaterial({ color: this.color });
    this.cube = new THREE.Mesh(this.geometry, this.material);

    // Cube's edge highlight setup.
    this.edgeGeometry = new THREE.EdgesGeometry(this.cube.geometry);
    this.edgeMaterial = new THREE.LineBasicMaterial({
        color: 0xb70000, 
        linewidth: 1
    });
    this.wireframe = new THREE.LineSegments(this.edgeGeometry, this.edgeMaterial);

    /**
     * Function for drawing object.
     * 
     * @param {THREE.Scene} scene - Scene to add object too.
     */
    this.draw = function(scene) {
        // Position cube.
        this.cube.position.set(this.position.x, this.position.y, this.position.z);
        scene.add(this.cube);

        // Position wireframe.
        this.wireframe.position.set(this.position.x, this.position.y, this.position.z);
        scene.add(this.wireframe);
        
        var cameraForward = new THREE.Vector3(
            controls.target.x - camera.position.x,
            controls.target.y - camera.position.y,
            controls.target.z - camera.position.z
        );
        var camreaToObject = new THREE.Vector3(
            this.position.x - camera.position.x,
            this.position.y - camera.position.y,
            this.position.z - camera.position.z
        
        );
        
        // If in-front of camera, display nameplate.
        if (cameraForward.dot(camreaToObject) > 0) {
            var widthHalf = window.innerWidth / 2;
            var heightHalf = window.innerHeight / 2;

            // Copy my position and convert to screen coords
            var pos = this.position.clone();
            pos.project(camera);

            // Pos' axies goes from 0-1 only.
            // This will place it based on center of screen.
            pos.x = (pos.x * widthHalf) + widthHalf;
            pos.y = -(pos.y * heightHalf) + heightHalf;

            placeNameplate(this.name, pos.x, pos.y);
        } else {        // Behind camera, remove nameplate.
            destroyNameplate(this.name);
        }
    };
}