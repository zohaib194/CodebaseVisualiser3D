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

        var nodes = new Array();

        /**
         * Function ofr getting node index from nodes array.
         * @param {string} name - name of node to fetch.
         */
        var getNodeIndex = function(name) {
            // Find node based on name.
           return nodes.findIndex((node) => { return (node.getName() == name) });
        }

        /**
         * Getter for nodes array.
         */
        var getNodes = function() {
            return nodes;
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
         * Function for adding a node to the graph.
         * @param {Node} node - Node object to be added.
         */
        var addNode = function(node) {
            // If node exists (has same name). Abort.
            if (getNodeIndex(node.getName()) >= 0 && node.type != "scope") {
                console.log(
                    LOCALE.getSentence("fdg_node_exists") + ": " + node.getName()
                );
                return;
            }

            // New node exists, add it.
            if (typeof node !== "undefined") {
                // Give back the index it appears in.
                // Push appends to the back and returns
                // the new lenght, hence "- 1".
                return nodes.push(node) - 1;
            }
        }

        /**
         * Function for adding a link/edge between nodes.
         * @param {int} indexFrom - Index of start node in FDG nodes array.
         * @param {int} indexTo - Index of end node in FDG nodes array.
         * @param {LinkProperties} link - LinkProperties object.
         */
        var addLink = function(indexFrom, indexTo, linkProp) {
            // Nodes doesn't exists, nodes are the same,
            // or both nodes has a link to eachother, abort linking!
            if (
                typeof nodes[indexFrom] === "undefined" ||
                typeof nodes[indexTo] === "undefined" ||
                indexFrom === indexTo ||
                (nodes[indexFrom].getLinks().has(indexTo) &&
                nodes[indexTo].getLinks().has(indexFrom))
            ) {
                return;
            }

            // Add bi-directional links.
            nodes[indexFrom].setLink(indexTo, linkProp);
            nodes[indexTo].setLink(indexFrom, linkProp);
        }

        /**
         * Function for executing the FDG algorithm.
         * @param {int} iterations - How many iterations the graph should run.
         */
        var execute = function(iterations) {
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
            addNode: addNode,
            addLink: addLink,
            execute: execute
        }
    }
);