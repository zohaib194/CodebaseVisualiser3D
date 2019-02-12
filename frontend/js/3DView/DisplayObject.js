/**
 * A generic object used for representing functions and other syntax.
 * @constructor
 * 
 * @param {THREE.Vector3} position - Position the cube willl be placed at.
 * @param {Integer|Hex} color - The color of the cube, preferrably not black.
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
    };
}