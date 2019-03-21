/**
 * Function for making a node object.
 * Represents any datastructure (function, class, namespace, variable, etc.).
 * @constructor
 * @param {THREE.Vector3()} position - Position of the node in the world.
 * @param {string} name - Name of the node (identifier).
 * @param {float} size - Size of the node
 * @param {string} type - Type of the node.
 */
var Node = (function(pos, name, size, type) {
    var index;
    var position = pos;
    var size = size;
    var children = Array();
    var parent = null;
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
        leftIndex,
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
        var indexRange = {min: null, max: null};

        // Run though every link
        links.forEach(function(link, nodeIndex) {
            //Check if connected node is in same scope
            var node = nodes.get[nodeIndex];
            if (typeof node !== "unidentified") {

                // Get the attractive force vector.
                diff.subVectors(
                    node.getPosition(),
                    position
                );

                // Take length before normalization.
                dist = diff.length();
                diff.normalize();

                // Choose physical rule based on attractive force.
                // Positive = attractive, negative = repulsive.
                if (link.attraction >= 0) {
                    // Attractive forces are based on logarithmic spring strenghts.
                    forceScalar = (
                        link.attraction * Math.log10(
                            dist / (minDistance + size + 5000 + node.getSize())
                        )
                    );
                } else {    // Repulsive force.
                    // Repulsive forces are based on Hookes law (inverse square law).
                    forceScalar = (link.attraction *
                        (
                            (maxDistance + size + 5000 + node.getSize()) /
                            Math.pow(dist, 2)
                        )
                    );
                }

                // Sum attractive and repulsive forces to total force
                force.add(diff.multiplyScalar(forceScalar));
            }
        });

        // Add gravitational force to center graph
        // on gravitational center.
        var gravity = new THREE.Vector3(0,0,0).subVectors(
            gravityCenter,
            position
        );

        // Return force with added gravity.
        return force.add(gravity.normalize().multiplyScalar(gravityForce));
    }

    var isNode = function(){
        return true;
    }

    /**
     * Gets the global position given by its parents position and itself.
     *
     * @return     {Three.Vector3}  The position.
     */
    var getPosition = function() {
        if (parent != null && parent.isNode()) {
            var pos = new THREE.Vector3(0, 0, 0);
            pos.addVectors(parent.getPosition(), position);
            return pos;
        }
        else{
            return position;
        }
    };


    /**
     * Getter for name.
     */
    var getName = function() {
        return metadata.name;
    };

    /**
     * Gets the index.
     *
     * @return {number} The index.
     */
    var getIndex = function() {

        // If node has not gotten an index yet; get one.
        if (typeof index === "undefined") {
            index = 1;

            children.forEach( function(child, index) {
                index += child.getIndex();
            });
        }

        return index;
    }

    /**
     * getter for size
     */
     var getSize = function(){
        return size;
     }

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
     * Gets the parent node.
     *
     * @return     {Node}  The parent.
     */
    var getParent = function(){
        return parent;
    };

    /**
     * Gets the child nodes.
     *
     * @return {Array}  The children.
     */
    var getChildren = function(){
        return children;
    }

    /**
     * Gets the all sucessors.
     */
    var getSuccessors = function(level){
        var successors = new Array();
        children.forEach( function(child, index) {
            successors = successors.concat(child.getSuccessors(level+1));
            successors.push(child);
        });
        return successors;
    }

    /**
     * Gets the siblings including itself.
     */
    var getSiblings = function(){
        if (parent != null) {
            parent.getChildren();
        }
    }

    /**
     * Gets the node index by name if the node is this node or one of its children.
     *
     * @param  {string} nodeName - The name of the node to find.
     * @return {number} The node index, -1 if node can not be found.
     */
    var getNodeIndex = function(nodeName){
        // Check if requested node is self
        if (name = nodeName) {
            return index;

        } else {                                    // Check if requested node is amongst children.

            var nodeIndex = -1;                     // Defaults to not found node.
            children.forEach((child, index) =>{
                nodeIndex = child.getNodeIndex(nodeName);
                if (nodeIndex != -1) {              // Check if child found node.
                    return
                }
            });

            return nodeIndex;
        }

        return -1;
    }

    /**
     * Gets the node based on index.
     *
     * @param  {number} requestedIndex - The requested node index
     * @return {object} The node.
     */
    var getNode = function(requestedIndex, level){
        //console.log("\t".repeat(level) + index + " ("+requestedIndex+")");
        var localOffset = 0;                                // Used to calculate relative index.
        var requestedNode = null;
        if (index-1 == requestedIndex) {                    // Check if self in requested node.
            //console.log("found");
            return this
        }

        children.forEach( function(child, i) {              // Check if children is requested node.
            localIndex = child.getIndex();
            if ((localIndex-1)+localOffset > requestedIndex){// Check if requested is within childs range.
                localOffset += localIndex;
            }else {
                // Child cointain range with requested node.
                requestedNode = child.getNode(requestedIndex - localOffset);
                return;
            }
        });
        return requestedNode;                               // indicate that node was not found
    }

    /**
     * Adds a child node.
     *
     * @param {Node} child - The child node to add
     */
    var addChild = function(child){
        if (typeof child === "object" && child.isNode()) {
            child.setIndex();
            children.push(child);
        }else{
            console.log("Could not add to FDGTree: not a Node");
        }
    }

    /**
     * Sets the index.
     *
     * @param {number} newIndex - The new index.
     */
    var setIndex = function(){
        index = subTreeSize() +1;
    }

    /**
     * Calculates size of nodes subtree by looking at the
     * childrens local index in the subtree
     * @return {number} size of subtree.
     */
    var subTreeSize = function(){
        var size = 0;
        children.forEach( function(child, index) {
            size += child.getIndex();
        });
        return size;
    }

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

    var setParent = function(newParent){
        parent = newParent
    }

    // Expose private functions for global use.
    return {
        isNode: isNode,
        getTotalForce: getTotalForce,
        getPosition: getPosition,
        getSize: getSize,
        getName: getName,
        getIndex: getIndex,
        getNode: getNode,
        getType: getType,
        getLinks: getLinks,
        getParent: getParent,
        getChildren: getChildren,
        getSiblings: getSiblings,
        getSuccessors: getSuccessors,
        getNodeIndex: getNodeIndex,
        addChild: addChild,
        setIndex: setIndex,
        setPosition: setPosition,
        setParent: setParent,
        setName: setName,
        setType: setType,
        setLink: setLink
    };
});