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
    maxSize,
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
         * Function ofr getting node index from nodes array.
         * @param {string} name - name of node to fetch.
         */
        var getNodeIndex = function(name) {
            // Find node based on name.
           return tree.root.getNodeIndex(name);
        }

        /**
         * Getter for nodes array.
         */
        var getNodes = function() {
            return tree.root.getSuccessors(0);
        }

        /**
         * Getter for maximum size of the graph.
         */
        var getMaxSize = function() {
            return maxSize;
        }

        /**
         * Sets the maximum size.
         *
         * @param {float} maxSize - The maximum size of the graph
         */
        var setMaxSize = function(maxSize) {
            maxSize = maxSize;
        }

        /**
         * Function for setting the tree for creating the fdg graph.
         * @param {Node} newRoot - Root node of the fdg problem.
         */
        var setTree = function(newRoot) {
           tree.root = newRoot;
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
            //console.log("From: " + nodeFrom);
            //console.log("To: " + nodeTo);
            // Nodes doesn't exists, nodes are the same,
            // or both nodes has a link to eachother, abort linking!
            if (
                nodeFrom === null ||
                nodeTo === null ||
                indexFrom === indexTo ||
                (nodeFrom.getLinks().has(indexTo) &&
                nodesTo.getLinks().has(indexFrom))
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
                for (x = 0; x < nodes.length; x++) {
                    // Calculate new position of node and update node.
                    var xNewPos = new THREE.Vector3(0, 0, 0);
                        xNewPos.addVectors(
                            nodes[x].getPosition(),
                            nodes[x].getTotalForce(
                                // Send a deep copy of array!
                                [...nodes],
                                subtreeOffset,
                                minDistance,
                                maxDistance,
                                gravityForce,
                                gravityCenter
                            )
                        );
                        nodes[x].setPosition(xNewPos);
                }
            }
        }

        // Expose private functions for global use.
        return {
            getNodeIndex: getNodeIndex,
            getNodes: getNodes,
            getMaxSize: getMaxSize,
            setMaxSize: setMaxSize,
            setTree: setTree,
            addLink: addLink,
            execute: execute
        }
    }
);