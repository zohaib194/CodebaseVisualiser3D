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
            pointer-events: none;
        `;
    }

    // Set position to be near the threejs object with name "name".
    // Using 'px' here cause it's the acutal screen position.
    nameplate.style.left = x_pos + 'px';
    nameplate.style.top = y_pos + 'px';

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
    if (nameplate != null) { // Delete if it exists.
         nameplate_container.removeChild(nameplate);
    }
}

/**
 * A generic object used for representing functions and other syntax.
 * @constructor
 *
 * @param {THREE.Vector3} position - Position the cube willl be placed at.
 * @param {Integer} color - The color of the cube, preferrably not black.
 * @param {string} name - Name of the object to be display on hover (not active).
 * @param {THREE.Geometry} geometry - Mesh to display. Defualt white cube (0.1,0.1,0.1).
 */
var Drawable = (function (
        pos,
        color,
        name,
        geometry,
        parent
    ) {
    if (typeof geometry === "undefined") {
        console.log(
            name + ": " + LOCALE.getSentence("geometry_missing_type")
        );
        return;
    }

    // Drawable setup.
    var mesh = new THREE.Mesh(geometry, new THREE.MeshBasicMaterial({ color: color, transparent: true, opacity: 0.5}));
    // Position THREE.js drawable.
    mesh.position.set(pos.x, pos.y, pos.z);
    mesh.name = name;
    mesh.frustumCulled = false;
    parent.add(mesh);

    // Drawable's edge highlight setup.
    var edgeGeometry = new THREE.EdgesGeometry(mesh.geometry);
    var edgeMaterial = new THREE.LineBasicMaterial({
        color: STYLE.getDrawables().edge.color,
        linewidth: 1
    });
    var wireframe = new THREE.LineSegments(edgeGeometry, edgeMaterial);
    wireframe.name = mesh.name + " | Wireframe";
    // Position wireframe.
    wireframe.position.set(pos.x, pos.y, pos.z);
    parent.add(wireframe);

    /**
     * Function for drawing drawable object.
     */
    var draw = function() {
        // Has no object to display, abort.
        if (typeof mesh === "undefined") {
            console.log(LOCALE.getSentence("displayobject_undefined"));
            return;
        }

        // Calc camera forward.
        var cameraForward = new THREE.Vector3(0, 0, 0);
        cameraForward.subVectors(controls.target, camera.position);

        // Calc vector from camera to object.
        var cameraToObject = new THREE.Vector3(0, 0, 0);
        cameraToObject.subVectors(mesh.position, camera.position);

        // If object is in-front of camera, display nameplate.
        if (cameraForward.dot(cameraToObject) > 0) {
            var widthHalf = window.innerWidth / 2;
            var heightHalf = window.innerHeight / 2;

            var worldUp = new THREE.Vector3(0, 1, 0);
            var cameraRight = cameraForward.cross(worldUp);
            cameraRight.normalize();

            worldUp.multiplyScalar(0.2);
            cameraRight.multiplyScalar(0.2);

            // Copy my position (+ offset) and convert to screen coords.
            scene.updateMatrixWorld();
            var posNameplate = new THREE.Vector3().setFromMatrixPosition(mesh.matrixWorld);
            posNameplate.add(cameraRight);
            posNameplate.add(worldUp);
            posNameplate.project(camera);

            // Pos' axies goes from 0-1 only.
            // This will place it based on center of screen.
            posNameplate.x = (posNameplate.x * widthHalf) + widthHalf;
            posNameplate.y = -(posNameplate.y * heightHalf) + heightHalf;

            placeNameplate(mesh.name, posNameplate.x, posNameplate.y);
        } else {        // Behind camera, remove nameplate.
            destroyNameplate(mesh.name);
        }
    };

    /**
     * Gets the mesh.
     *
     * @return     {Object}  The mesh.
     */
    var getMesh = function() {
        return mesh;
    }

    // Expose private functions for global use.
    return {
        draw: draw,
        getMesh: getMesh,
    }
});