/**
 * Manages DisplayObjects for displaying.
 * @constructor
 */
var DisplayManager = (function() {
    var drawables = new Map();

    /**
     * Add objects based on given type.
     *
     * @param {string} type - E.g: [function|class|namespace].
     * @param {Drawable} object - THREE.Mesh to be added.
     */
    var addObject = function(type, object) {
        // No key matching "type" is found.
        if (!drawables.has(type)) {
            // Make list and add object under "type".
            drawables.set(type, [object]);
        } else {    // Key exists already.
            // Add new object to "type" list.
            Array.prototype.push.apply(drawables.get(type), [object]);
        }
        return drawables.get(type).length-1;
    };

    /**
     * Function for drawing all registered drawable objects.
     */
    var draw = function() {
        if (drawables != null) {                        // Map exists.
            drawables.forEach((value, key, map) => {    // Run though map.
                if (value != null && value.length > 0){ // Object list exists.
                    value.forEach(object => {           // Run though objects.
                        object.draw();
                    });
                }
            });
        }
    };

    /**
     * Function for selecting proper type from configuration.
     * @param {string} type - The type of node.
     */
    var getDrawableGeometry = (function(type, size) {
        switch(type) {
            case "cube":
                return new THREE.BoxGeometry(0.5 * size, 0.5 * size, 0.5 * size);
            case "sphere":
                return new THREE.SphereGeometry(1 * size, 32, 32);
            case "cylinder":
                return new THREE.CylinderGeometry(0.5 * size, 0.5 * size, 1 * size, 32);
            case "cone":
                return new THREE.ConeGeometry(0.5 * size, 1 * size, 32);
            case "dodecahedron":
                return new THREE.DodecahedronGeometry(0.5 * size);
            case "icosahedron":
                return new THREE.IcosahedronGeometry(0.5 * size);
            case "octahedron":
                return new THREE.OctahedronGeometry(0.5 * size);
            case "tetrahedron":
                return new THREE.TetrahedronGeometry(0.5 * size);
        }
    });

    /**
     * Takes in a tree of nodes and adds them as successors of scene.
     *
     * @param {Object} tree - Tree of nodes where a childs node will be added as a child
     *  of its parents mesh in the scenegraph.
     */
    var setSceneGraph = function(tree, parent){
        var nodeType = tree.getType();
        var supportedType = false;
        var drawableGeometry;
        var drawableColor;

        if(nodeType !== "root"){

            // Select shape and color based on node type.
            switch (nodeType) {
                case "function": {
                    drawableColor = STYLE.getDrawables().function.color;
                    drawableGeometry = getDrawableGeometry(
                        STYLE.getDrawables().function.shape,
                        tree.getSize()
                    );
                    supportedType = true;
                    break;
                }
                case "class": {
                    drawableGeometry = getDrawableGeometry(
                        STYLE.getDrawables().class.shape,
                        tree.getSize()
                    );
                    drawableColor = STYLE.getDrawables().class.color;
                    supportedType = true;
                    break;
                }
                case "namespace": {
                    drawableGeometry = getDrawableGeometry(
                        STYLE.getDrawables().namespace.shape,
                        tree.getSize()
                    );
                    drawableColor = STYLE.getDrawables().namespace.color;
                    supportedType = true;
                    break;
                }
                case "variable": {
                    drawableGeometry = getDrawableGeometry(
                        STYLE.getDrawables().variable.shape,
                        tree.getSize()
                    );
                    drawableColor = STYLE.getDrawables().variable.color;
                    supportedType = true;
                    break;
                }
                default: {   // Unsupported node type, mention this!
                    console.log(
                        LOCALE.getSentence("geometry_invalid_type") + ": " + tree.getType()
                    );
                    break;
                }
            }

            // Found a supported type.
            document.getElementById("status").innerHTML =
                LOCALE.getSentence("userinfo_ready_display");

            if(typeof parent === "undefined"){
                parent = scene;
            }

            var selfDrawable = new Drawable(
                tree.getPosition(),
                drawableColor,
                tree.getName(),
                drawableGeometry,
                parent
            );

            if (supportedType) {
                // Add it for display.
                var drawableIndex = displayMgr.addObject(
                    tree.getType(),
                    selfDrawable
                );
                tree.setDrawableIndex(drawableIndex);

            }
            children = tree.getChildren();
            children.forEach( function(subTree, index) {
                setSceneGraph(subTree, selfDrawable.getMesh());
            });

        }else{      // if typeof tree is root
            children = tree.getChildren();
            children.forEach( function(subTree, index) {
                setSceneGraph(subTree, scene);
            });

        }
    };

    var setLinks = function(tree, scene){
        scene.updateMatrixWorld();
        var nodes = tree.getSuccessors();

        nodes.forEach( function(node, index) {

            if (node.getDrawableIndex() != -1) {

                node.getLinks().forEach((strength, otherIndex) => {

                    if (nodes[otherIndex].getDrawableIndex() != -1) {

                        var material = new THREE.LineBasicMaterial({
                            color: STYLE.getDrawables().link.color
                        });

                        var geometry = new THREE.Geometry();

                        geometry.vertices.push(
                            new THREE.Vector3().setFromMatrixPosition(drawables.get(node.getType())[node.getDrawableIndex()].getMesh().matrixWorld),
                            new THREE.Vector3().setFromMatrixPosition(drawables.get(nodes[otherIndex].getType())[nodes[otherIndex].getDrawableIndex()].getMesh().matrixWorld)
                        );

                        var line = new THREE.Line(geometry, material);
                        scene.add(line);
                    }
                });
            }
        });
    }

    // Expose private functions for global use.
    return {
        addObject: addObject,
        setSceneGraph: setSceneGraph,
        setLinks: setLinks,
        draw: draw
    }
});