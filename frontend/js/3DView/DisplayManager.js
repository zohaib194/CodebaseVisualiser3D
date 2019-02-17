/**
 * Manages DisplayObjects for displaying.
 * @constructor
 */
function DisplayManager() {
    this.objects = new Map();

    /**
     * Add objects based on given type.
     *
     * @param {string} type - E.g: [function|statement|expression|if|while].
     * @param {DisplayObject} object - DisplayObject to be added.
     */
    this.addObject = function(type, object) {
        // No key matching "type" is found.
        if (!this.objects.has(type)) {
            // Make list and add object under "type".
            this.objects.set(type, [object]);
        } else {    // Key exists already.
            // Add new object to "type" list.
            Array.prototype.push.apply(this.objects.get(type), [object]);
        }
    };

    /**
     * Function for drawing all registered objects.
     */
    this.draw = function() {
        if (this.objects != null) {                     // Map exists.
            this.objects.forEach((value, key, map) => { // Run though map.
                if (value != null && value.length > 0){ // Object list exists.
                    value.forEach(object => {           // Run though objects.
                        object.draw();
                    });
                }
            });
        }
    };
}