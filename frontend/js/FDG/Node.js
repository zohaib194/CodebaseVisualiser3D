/**
 * Function for making a node object. 
 * Represents any datastructure (function, class, namespace, variable, etc.).
 * @constructor
 * @param {THREE.Vector3()} position - Position of the node in the world.
 * @param {string} name - Name of the node (identifier).
 * @param {string} type - Type of the node.
 */
var Node = (function(pos, name, type) {
    var position = pos;
    var metadata = {
        name: name,
        type: type
    };
    
    var links = new Map();
    
    /**
     * Calculates total force on node.
     * @param {Node[]} nodes - Array of all nodes in fdg object.
     * @param {int} minDistance - Min distance between nodes.
     * @param {int} maxDistance - Max distance between nodes.
     * @param {THREE.Vector3} gravity - Force of gravity. Defaults to 0.
     * @param {THREE.Vector3} gravityCenter - Center of gravity. Defualts to (0, 0, 0).
     */
    var getTotalForce = function(
        nodes, 
        minDistance, 
        maxDistance, 
        gravityForce = 0, 
        gravityCenter = new THREE.Vector3(0, 0, 0)
    ) {
            
        // Nodes doesn't exist or empty, abort.
        if (nodes === "undefined" || nodes.length <= 0) {
            return;
        }
        
        var force = new THREE.Vector3(0, 0, 0);
        var forceScalar = 0;
        var diff = new THREE.Vector3(0, 0, 0);
        var dist = 0;
        
        // Run though every link
        links.forEach((link, nodeIndex) => {
            // Get the attractive force vector.
            diff.subVectors(
                nodes[nodeIndex].getPosition(),
                position
            );
            
            // Take length before normalization.
            dist = diff.length();
            diff.normalize();

            // Choose physical rule based on attractive force.
            // Positive = attractive, negative = repulsive.
            if (link.attraction >= 0) {
                // Attractive forces are based on logarithmic spring strenghts.
                forceScalar = (link.attraction * Math.log10(dist / minDistance));
            } else {    // Repulsive force.
                // Repulsive forces are based on Hookes law (inverse square law).
                forceScalar = (link.attraction * (maxDistance / Math.pow(dist, 2)));
            }

            // Sum attractive and repulsive forces to total force
            force.add(diff.multiplyScalar(forceScalar));
        });

        // Add gravitational force to center graph 
        // on gravitational center.
        var gravity = new THREE.Vector3().subVectors(
            gravityCenter, 
            position
        );
        
        // Return force with added gravity.
        return force.add(gravity.normalize().multiplyScalar(gravityForce));
    }

    /**
     * Getter for position.
     */
    var getPosition = function() {
        return position;
    };

    /**
     * Getter for name.
     */
    var getName = function() {
        return metadata.name;
    };

    /**
     * Getter for type.
     */
    var getType = function() {
        return metadata.type;
    };

    /**
     * Getter for links array.
     */
    var getLinks = function() {
        return links;
    };

    /**
     * Setter for position.
     */
    var setPosition = function(pos) {
        position.set(pos.x, pos.y, pos.z);
    };
    
    /**
     * Setter for name.
     */
    var setName = function(name) {
        return metadata.name = name;
    };

    /**
     * Getter for type.
     */
    var setType = function(type) {
        return metadata.type = type;
    };

    /**
     * Getter for link.
     */
    var setLink = function(key, value) {
        links.set(key, value);
    };

    // Expose private functions for global use.
    return {
        getTotalForce: getTotalForce,
        getPosition: getPosition,
        getName: getName,
        getType: getType,
        getLinks: getLinks,
        setPosition: setPosition,
        setName: setName,
        setType: setType,
        setLink: setLink
    };
});