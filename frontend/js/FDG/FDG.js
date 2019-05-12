/**
 * Function for making a Force-Directed-Graph object, manages nodes and links.
 * @param {float} minDistance - Minimum desired distance between nodes.
 * @param {float} maxDistance - Maximum desired distance between nodes.
 * @param {float} gravityForce - Gravitational attraction to "gravityCenter".
 * @param {THREE.Vector3} gravityCenter - Center of gravity. default (0,0,0).
 */
var FDG = (function FDG(
    minDistance,
    maxDistance,
    gravityForce,
    gravityCenter = new THREE.Vector3(0, 0, 0)
    ) {
        var minDistance = minDistance;
        var maxDistance = maxDistance;
        var maxSize = maxSize;
        var gravityForce = gravityForce;
        var gravityCenter = gravityCenter;

        var tree = {root: new Node(
            THREE.Vector3(0,0,0),
            "root",
            null,
            "root"
        )}

        /**
         * Gets the node index.
         *
         * @param      {string}  name    The name
         * @return     {number}  The node index.
         */
        var getNodeIndex = function(name) {
            // Find node based on name.
           return tree.root.getNodeIndex(name);
        }

        /**
         * Gets the nodes.
         *
         * @return     {array}  The nodes.
         */
        var getNodes = function() {
            return tree.root.getSuccessors(0);
        }

        /**
         * Gets the maximum size.
         *
         * @return     {number}  The maximum size.
         */
        var getMaxSize = function() {
            return maxSize;
        }

        /**
         * Gets the root.
         *
         * @return     {Object}  The root.
         */
        var getRoot = function() {
            return tree.root;
        }

        /**
         * Function for setting the tree for creating the fdg graph.
         * @param {Node} newRoot - Root node of the fdg problem.
         */
        var setTree = function(newRoot) {
           tree.root = newRoot;
           maxSize = tree.root.getSize();
           finalize();
        }

        /**
         * Function for adding a link/edge between nodes.
         * @param {int} indexFrom - Index of start node in FDG nodes array.
         * @param {int} indexTo - Index of end node in FDG nodes array.
         * @param {LinkProperties} link - LinkProperties object.
         */
        var addLink = function(indexFrom, indexTo, linkProp) {
            nodeFrom = tree.root.getNode(indexFrom, 0);
            nodeTo = tree.root.getNode(indexTo, 0);
            // Nodes doesn't exists, nodes are the same,
            // or both nodes has a link to eachother, abort linking!
            if (
                nodeFrom === null ||
                nodeTo === null ||
                typeof nodeFrom === "undefined" ||
                typeof nodeTo === "undefined" ||
                indexFrom === indexTo ||
                (nodeFrom.getLinks().has(indexTo) &&
                nodeTo.getLinks().has(indexFrom))
            ) {
                console.log("Can not link nodes, either null, same or already connected");
                return;
            }
            // Add bi-directional links.
            nodeFrom.setLink(indexTo, linkProp);
            nodeTo.setLink(indexFrom, linkProp);
        }

        /**
         * Function for executing the FDG algorithm.
         * @param {int} iterations - How many iterations the graph should run.
         */
        var execute = function(iterations) {
            executeSubTree(iterations, tree.root, 0);
        }

        /**
         * Recursivly runs FDG algorithm on all its sub graphs
         *
         * @param {number}  iterations    - The iterations
         * @param {object}  subtree       - The tree
         * @param {number}  subtreeOffset - Global index offset from local
         */
        var executeSubTree = function(iterations, subtree, subtreeOffset){
            var children = subtree.getChildren();
            var size = subtree.getSize();
            var nodes = new Map();

            // recursivly run force directed graph
            var indexOffset = subtreeOffset;
            children.forEach( function(child, index) {
                executeSubTree(iterations, child, indexOffset);
                indexOffset += child.getIndex();
                nodes.set(indexOffset, child);
            });

            // Run though every node per every iteration.
            for (i = 0; i < iterations; i++) {
                nodes.forEach( function(node, index) {
                    // Calculate new position of node and update node.
                    var xNewPos = new THREE.Vector3(0, 0, 0);
                        xNewPos.addVectors(
                            node.getPosition(),
                            node.getTotalForce(
                                nodes,
                                subtreeOffset,
                                minDistance,
                                maxDistance,
                                size,
                                gravityForce,
                                gravityCenter
                            )
                        );
                        node.setPosition(xNewPos);
                });
            }
        }

        /**
         * Sets the global index of all nodes.
         */
        var finalize = function(){
            getNodes().forEach( function(node, index) {
                node.setFinalizedIndex(index);
            });
        }
        // Expose private functions for global use.
        return {
            getNodeIndex: getNodeIndex,
            getNodes: getNodes,
            getMaxSize: getMaxSize,
            getProjectRoot: getRoot,
            setTree: setTree,
            addLink: addLink,
            execute: execute
        }
    }
);