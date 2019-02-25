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

    // Expose private functions for global use.
    return {
        addObject: addObject,
        draw: draw
    }
});