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
    var finalizedIndex = -1;
    var position = pos;
    var size = size;
    var children = Array();
    var parent = null;
    var metadata = {
        name: name,
        type: type
    };
    var modelSpecificMetaData = null;
    var drawableIndex = -1;

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
        maxSize,
        gravityForce = 0.7,
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
        nodes.forEach(function(node, nodeIndex) {
            //Check if connected node is in same scope
            var link = links[nodeIndex];

            // Get the attractive force vector.
            diff.subVectors(
                node.getPosition(),
                position
            );

            // Take length before normalization.
            dist = diff.length();
            if (dist == 0) {
                dist = 0.1;
            }

            diff.normalize();


            if (typeof link !== "undefined") {
                // Attractive forces are based on logarithmic spring strenghts.
                forceScalar = (
                    link.attraction * Math.log10(
                        dist / (minDistance + size + node.getSize())
                    )
                );

            } else {    // Repulsive force.
                // Repulsive forces are based on Hookes law (inverse square law).
                forceScalar = (-1 *
                    (
                        (maxDistance + size + node.getSize()) /
                        Math.pow(dist, 2)
                    )
                );

            }

            // Sum attractive and repulsive forces to total force
            force.add(diff.multiplyScalar(forceScalar));
        });

        // Force the nodes to be within the maximum distance of center.
        // The force grows stronger the further away from center they are.
        // Add gravitational force to center graph
        // on gravitational center.
        var gravity = new THREE.Vector3(0,0,0).subVectors(
            gravityCenter,
            position
        );

        // Assure node is within boundaries.
        var distanceFromOrigin = gravity.length();
        if ( distanceFromOrigin > maxSize) {
            gravity.normalize().multiplyScalar(maxSize-0.1);
        }

        //console.log(leftIndex, minDistance, maxDistance, maxSize, gravityForce, gravityCenter);
        //console.log(dist, maxSize, maxDistance, gravityForce);
        gravityForce = (Math.log10(distanceFromOrigin + maxSize) - Math.log10(maxSize - distanceFromOrigin)) * gravityForce;
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
        return position;
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

    var getChildByNameAndType = function(name, type){
        var requestedChild = null;
        children.every(function(child, index){

            if(child.getName() === name && (child.getType() === type ||
                (type ==="class" && child.getType() === "variable"))){
                requestedChild = child;
                return false;
            }
            return true;
        });

        if(requestedChild != null && requestedChild.getType() != "variable"){
            // check if is variable
            if (requestedChild.getModelSpecificMetaData() != null) {
                var className = requestedChild.getModelSpecificMetaData().type;
                requestedChild = getChildByNameAndType(className, "class");
            }
        }

        return requestedChild;
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
        var localOffset = 0;                                // Used to calculate relative index.
        var requestedNode = null;
        if (typeof index !== "undefined" && (index-1 === requestedIndex)) {                    // Check if self is requested node.
            return this
        }
        children.every( function(child, i) {              // Check if children is requested node.
            localIndex = child.getIndex();
            if ((localIndex-1)+localOffset < requestedIndex){// Check if requested is within childs range.
                localOffset += localIndex;
            }else {
                // Child cointain range with requested node.
                requestedNode = child.getNode(requestedIndex - localOffset);
                return false;
            }
            return true;
        });
        return requestedNode;                               // indicate that node was not found
    }

    var getFinalizedIndex = function(){
        return finalizedIndex;
    }

    var getDrawableIndex = function(){
        return drawableIndex;
    }

    var getModelSpecificMetaData = function(){
        return modelSpecificMetaData;
    }

    var getEncapsulatingClass = function(){
        if( metadata.type === "class" ){
            return this;
        }else{
            return parent.getEncapsulatingClass();
        }
    }
    var setModelSpecificMetaData = function(newModelSpecificMetaData){
        modelSpecificMetaData = newModelSpecificMetaData;
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
        var subSize = subTreeSize();

        if (typeof subSize  === "undefined") {
            index = 1;

        }else{
            index = subSize +1;

        }
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
        //console.log("new position: ", position, " name: ", metadata.name);
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
    var setLink = function(linkToIndex, strength) {
        links.set(linkToIndex, strength);
    };

    var setParent = function(newParent){
        parent = newParent;
    }

    var setSize = function(newSize){
        size = newSize;
    }

    var setFinalizedIndex = function(index){
        finalizedIndex = index;
    }

    var setDrawableIndex = function(newDrawableIndex){
        drawableIndex = newDrawableIndex;
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
        getChildByNameAndType: getChildByNameAndType,
        getSiblings: getSiblings,
        getSuccessors: getSuccessors,
        getNodeIndex: getNodeIndex,
        getDrawableIndex: getDrawableIndex,
        getFinalizedIndex: getFinalizedIndex,
        getModelSpecificMetaData: getModelSpecificMetaData,
        getEncapsulatingClass: getEncapsulatingClass,
        addChild: addChild,
        setModelSpecificMetaData: setModelSpecificMetaData,
        setIndex: setIndex,
        setPosition: setPosition,
        setParent: setParent,
        setName: setName,
        setType: setType,
        setLink: setLink,
        setSize: setSize,
        setFinalizedIndex: setFinalizedIndex,
        setDrawableIndex: setDrawableIndex
    };
});