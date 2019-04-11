var indexStack = new Array();
var functionModels = new Map();
var indexToFunctionMap = new Map();

var fileCount = 0;
var parsedFileCount = 0;
var skippedFileCount = 0;

// Number of classes found in the project
var classCount = 0;

// Number of functions found in the project.
var functionCount = 0;

// Number of namespaces found in the project.
var namespaceCount = 0;

// Number of variable found in the project.
var variableCount = 0;

/**
 * Function to get new random position within a radius of a position.
 * @param {THREE.Vector3} center - Where to calculate position from.
 * @param {float} radius - Maximum offset from center
 */
function randomPosition(center, radius) {
    return new THREE.Vector3(
        // Gets a number between center - radius and center + radis
        Math.random() * (2 * radius) + center.x - radius,
        Math.random() * (2 * radius) + center.y - radius,
        Math.random() * (2 * radius) + center.z - radius
    );
}
/**
 * Function for linking the two last elements in indexStack.
 */
function linkElements() {
    // If I have a parent, add attractive link between us.
    if (indexStack.length >= 2) {
        fdg.addLink(
            indexStack[indexStack.length - 2],
            indexStack[indexStack.length - 1],
            new LinkProperties(1)
        );
    } else {    // Missing parrent, state so.
        console.log(LOCALE.getSentence("fdg_link_missing_parent"));
    }
}

/**
 * Links function calls.
 */
function linkFunctionCalls(){
    fdg.getNodes().forEach( function(node, callerIndex) {

        if(node.getType() == "function") {
            var fm = functionModels.get(node.getName());


            if(typeof fm.getCalls() !== "undefined") {
                fm.getCalls().forEach( function(callModel, callIndex) {
                    console.log("callModel: ", callModel);


                    var calleeNode = null;
                    var calleeIndex = -1;
                    var scopePath = [];
                    console.log("Initial: ", scopePath, " | ", scopePath.length )
                    if (typeof callModel.scopes !== "undefined"){
                        callModel.scopes.every( function(scope, scopeIndex) {

                            if (scopeIndex === 0 && scopePath.length === 0) {
                                if (scope.identifier == "this") {
                                    scopePath.push(node.getEncapsulatingClass());
                                }else{

                                    var nodeToSearch = node;

                                    // Where to search if called scope exist.
                                    while(scopeIndex == 0 && scopePath.length == 0){

                                        var foundScope = nodeToSearch.getChildByNameAndType(scope.identifier, scope.type);
                                        if (foundScope != null) {

                                            scopePath.push(foundScope);
                                        }else{

                                            nodeToSearch = nodeToSearch.getParent();

                                            if (nodeToSearch == null) {
                                                return false;
                                            }
                                        }
                                    }
                                }
                            } else {
                                var childNode = scopePath[scopePath.length - 1].getChildByNameAndType(scope.identifier, scope.type);
                                if (childNode != null) {
                                    scopePath.push(childNode);
                                }
                            }
                            return true;
                        });

                        if (scopePath.length > 0) {
                            calleeNode = scopePath[scopePath.length - 1].getChildByNameAndType(callModel.identifier, "function");
                            if (calleeNode != null) {

                                calleeIndex = calleeNode.getFinalizedIndex();
                            } else {
                                calleeIndex = scopePath[scopePath.length -1].getFinalizedIndex();
                            }
                        }
                    } else {

                        var nodeToSearch = node;
                        var provenNotToExist = false;
                        var foundNode = null;
                        // Where to search if called scope exist.
                        while(!provenNotToExist && !foundNode){

                            foundNode = nodeToSearch.getChildByNameAndType(callModel.identifier, "function");
                            if (foundNode == null) {

                                nodeToSearch = nodeToSearch.getParent();

                                if (nodeToSearch == null) {
                                    provenNotToExist = true;
                                }
                            }
                        }

                        if(foundNode != null){
                            calleeIndex = foundNode.getFinalizedIndex();
                        }
                    }

                    fdg.addLink(node.getFinalizedIndex(), calleeIndex, new LinkProperties(1));

                    console.log("Before: ", scopePath, " | ", scopePath.length)
                    scopePath = [];
                    console.log("After: ", scopePath, " | ", scopePath.length)
                });

            }
        }

    });



    /*indexToFunctionMap.forEach( function(callerIndex, callerFuncName) {

        // Get the function caller.
        funcModel = functionModels.get(callerFuncName);
        // Check the calls length.
        if(funcModel.getCalls().length == 0){
            return;
        }

        console.log("Caller: ", callerIndex, callerFuncName);

        // Loop through calls
        funcModel.getCalls().forEach( function(calleeFuncName, index) {

            calleeIndex = indexToFunctionMap.get(calleeFuncName)
            console.log("Callee: ", calleeIndex, calleeFuncName);



           /* if(functionModels.has(funcName)){

                fdg.addLink(
                    indexStack[callerIndex],
                    indexStack[calleeIndex],
                    new LinkProperties(1)
                );

            }
        });

    });*/
}

/**
 * Function for handling class data in JSONObject.
 * @param {JSONObject} classData - Data about a class.
 */
function handleClassData(classData, filename) {
    var children = [];
    if (typeof classData.access_specifiers !== "undefined") {
        // Handle all functions
        classData.access_specifiers.forEach( (object) => {
            children = children.concat(handleAccessSpecifiers(object, filename));
        });
    }
    var position = randomPosition({x:0, y:0, z:0}, 5);
    var size = 5;

    children.forEach( function(child, index) {
        size += child.getSize();
    });

    // Add node and save index.
    nodeSelf = new Node(
        position,
        classData.name,
        size,
        "class"
    );

    classCount++;

    children.forEach( function(child, index) {
        nodeSelf.addChild(child);
        child.setParent(nodeSelf);
    });

    return nodeSelf;
}

/**
 * Function for handling access specifiers.
 * @param {JSONObject} accessData - Data about a access rights.
 */
function handleAccessSpecifiers(accessData, filename) {
    var children = handleCodeData(accessData, filename);

    children.forEach( function(child, index) {
        child.access = accessData.name;
    });

    return children;
}

/**
 * Function for handling namespace data in JSONObject.
 * @param {JSONObject} namespaceData - Data about namespace.
 */
function handleNamespaceData(namespaceData, filename) {
    // Handle any children.
    var children = handleCodeData(namespaceData, filename);
    var position = randomPosition({x:0, y:0, z:0}, 5);
    var size = 5;

    children.forEach( function(child, index) {
        size += child.getSize();
    });

    nodeSelf = new Node(
        position,
        namespaceData.name,
        size,
        "namespace"
    )

    namespaceCount++;

    children.forEach( function(child, index) {
        nodeSelf.addChild(child);
        child.setParent(nodeSelf);
    });

    return nodeSelf;
}

/**
 * Function for handling function data in JSONObject.
 * @param {JSONObject} functionData - Data about a function JSONObject.
 */
function handleFunctionData(functionData, filename) {
    // Handle any children.
    var children = handleCodeData(functionData, filename);
    var position = randomPosition({x:0, y:0, z:0}, 5);
    var size = 5;

    nodeSelf = new Node(
        position,
        functionData.name,
        size,
        "function"
    );

    // Map current node to the index.
    //indexToFunctionMap.set(functionData.function.name, index);

    // Save the function data in function model.
    functionModels.set(
        functionData.name,
        new FunctionMetaData(
            filename,
            functionData.function_body.calls,
            functionData.returnType,
            functionData.declid,
            functionData.start_line,
            functionData.end_line
        )
    );

    functionCount++;

    children.forEach( function(child, index) {
        nodeSelf.addChild(child);
        child.setParent(nodeSelf);
    });

    return nodeSelf;
}

function handleVariableData(variableData, filename) {
    // Handle any children.
    var children = handleCodeData(variableData, filename);
    var position = randomPosition({x:0, y:0, z:0}, 5);
    var size = 5;

    children.forEach( function(child, index) {
        size += child.getSize();
    });

    nodeSelf = new Node(
        position,
        variableData.name,
        size,
        "variable"
    )

    nodeSelf.setModelSpecificMetaData({
        type: variableData.type,
    });

    variableCount++;

    children.forEach( function(child, index) {
        nodeSelf.addChild(child);
        child.setParent(nodeSelf);
    });

    return nodeSelf;
}

/**
 * Function for handling code data in JSONObject.
 * @param {JSONObject} codeData  - Data about general code.
 */
function handleCodeData(codeData, filename) {
    var children = new Array();
    if (typeof codeData.using_namespaces !== "undefined") {
        console.log("Something: ");
    }
    if (typeof codeData.namespaces !== "undefined") {
        // Handle all namespaces
        codeData.namespaces.forEach((object) => {
           children.push(handleNamespaceData(object, filename));
        });
    }
    if (typeof codeData.classes !== "undefined") {
        // Handle all classes
        codeData.classes.forEach((object) => {
           children.push(handleClassData(object, filename));
        });
    }
    if (typeof codeData.functions !== "undefined") {
        // Handle all functions
        codeData.functions.forEach((object) => {
           children.push(handleFunctionData(object, filename));
        });
    }

    if (typeof codeData.variables !== "undefined") {
        // Handle all functions
        codeData.variables.forEach((object) => {
           children.push(handleVariableData(object, filename));
        });
    }


    return children;
}

/**
 * Function for handling project json data.
 * @param {JSONObject} projectData - Data about project files as JSONObject.
 */
function handleProjectData(projectData) {
    // Reset metadata for current project
    classCount = 0;
    functionCount = 0;
    namespaceCount = 0;
    variableCount = 0;
    lineCount = 0;
    var size = 5;

    var root = new Node(
        new THREE.Vector3(0,0,0),
        "root",
        null,
        "root"
    );

    // Handle every file given.
    projectData.files.forEach((file) => {

        // If file is not parsed, skip it
        // ("return" returns from lambda not forEach).
        if (file.parsed != true) {
            return;
        }

        // File is parsed correctly, process it.
        lineCount += file.linesInFile;
        var children = handleCodeData(file, file.file_name);

        children.forEach( function(child, index) {
            root.addChild(child);
            child.setParent(root);
            size += child.getSize();
        });

    });
    root.setSize(size);
    fdg.setTree(root);

    windowMgr.setFunctionCount(functionCount);
    windowMgr.setClassCount(classCount);
    windowMgr.setNamespaceCount(namespaceCount);
    windowMgr.setVariableCount(variableCount);
    windowMgr.setLineCount(lineCount);

    linkFunctionCalls();
}