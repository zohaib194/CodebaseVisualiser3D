/**
 * Function for making a Force-Directed-Graph object, manages nodes and links.
 * @param {float} minDistance - Minimum desired distance between nodes.
 * @param {float} maxDistance - Maximum desired distance between nodes.
 * @param {float} gravityForce - Gravitational attraction to "gravityCenter".
 * @param {THREE.Vector3} gravityCenter - Center of gravity. default (0,0,0).
 */
function FDG(
    minDistance, 
    maxDistance, 
    gravityForce, 
    gravityCenter = new THREE.Vector3(0, 0, 0)) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.gravityForce = gravityForce;
        this.gravityCenter = gravityCenter;
        
        this.nodes = new Array();

    this.getNodeIndex = function(name) {
        // Find node based on name.
        this.nodes.findIndex((node) => {
            return (node.name == name);
        });
    }

    /**
     * Function for adding a node to the graph.
     * @param {Node} node - Node object to be added.
     */
    this.addNode = function(node) {
        // If node exists (has same name). Abort.
        if (this.getNodeIndex(node.name) < 0) {
            console.log("ERROR: Node with name: " + node.name + " already exsist!");
            return;
        }

        // First element (array doesn't exist).
        if (this.nodes === "undefined") {
            // Initialize array with node.
            this.nodes = new Array(node);
        } else {       // Not first element, append it.
            // Unique name/identifier, add it.
            Array.prototype.push.apply(this.nodes, [node]);
        }
        
        // Give back the index it appears in.
        return this.nodes.indexOf(node);
    }

    /**
     * Function for add a link/edge between nodes.
     * @param {int} indexFrom - Index of start node in FDG nodes array.
     * @param {int} indexTo - Index of end node in FDG nodes array.
     * @param {LinkProperties} link - LinkProperties object.
     */
    this.addLink = function(indexFrom, indexTo, linkProp) {
        // Nodes doesn't exists, nodes are the same, 
        // or both nodes has a link to eachother, abort linking!
        if (typeof this.nodes[indexFrom] === "undefined" || 
            typeof this.nodes[indexTo] === "undefined" || 
            indexFrom === indexTo || 
            (this.nodes[indexFrom].links.has(indexTo) && 
            this.nodes[indexTo].links.has(indexFrom))
        ) {
            return;
        }

        // Add bi-directional links.
        this.nodes[indexFrom].links.set(indexTo, linkProp);
        this.nodes[indexTo].links.set(indexFrom, linkProp);
    }
    
    /**
     * Function for executing the FDG algorithm.
     * @param {int} iterations - How many iterations the graph should run.
     */
    this.execute = function(iterations) {
        //var clock = new THREE.Clock();
        //clock.start()
        for (i = 0; i < iterations; i++) {
            //var dt = clock.getDelta();
            //console.log(dt + "\n");

            for (j = 0; j < this.nodes.length; j++) {
                this.nodes[j].position.add(this.nodes[j].getTotalForce(
                    // Send a deep copy of array!
                    [...this.nodes],
                    this.minDistance, 
                    this.maxDistance, 
                    this.gravityForce, 
                    this.gravityCenter
                ));
            }
        }
    }
}