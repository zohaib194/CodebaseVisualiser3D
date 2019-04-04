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

/**
 * Function to get new random position within a radius of a position.
 * @param {THREE.Vector3} center - Where to calculate position from.
 * @param {float} radius - Maximum offset from center
 */
function randomPosition(center, radius) {
    return new THREE.Vector3(
        // Gets a number between center - radius and center + radis
        Math.random() * (2 * radius) + (center.x - radius),
        Math.random() * (2 * radius) + (center.y - radius),
        Math.random() * (2 * radius) + (center.z - radius)
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
    indexToFunctionMap.forEach( function(callerIndex, callerFuncName) {

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
            */
        });

    });
}

/**
 * Function for handling class data in JSONObject.
 * @param {JSONObject} classData - Data about a class.
 */
function handleClassData(classData, filename) {
    var children = handleCodeData(classData.Class, filename);
    var position = randomPosition({x:0, y:0, z:0}, 5);
    var size = 5;

    children.forEach( function(child, index) {
        size += child.getSize() * 5;
    });

    // Add node and save index.
    nodeSelf = new Node(
        position,
        classData.Class.name,
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
 * Function for handling namespace data in JSONObject.
 * @param {JSONObject} namespaceData - Data about namespace.
 */
function handleNamespaceData(namespaceData, filename) {
    // Handle any children.
    var children = handleCodeData(namespaceData.namespace, filename);
    var position = randomPosition({x:0, y:0, z:0}, 5);
    var size = 5;

    children.forEach( function(child, index) {
        size += child.getSize() * 5;
    });

    nodeSelf = new Node(
        position,
        namespaceData.namespace.name,
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
    var children = handleCodeData(functionData.function, filename);
    var position = randomPosition({x:0, y:0, z:0}, 5);
    var size = 5;

    nodeSelf = new Node(
        position,
        functionData.function.name,
        size,
        "function"
    );

    // Map current node to the index.
    //indexToFunctionMap.set(functionData.function.name, index);

    // Save the function data in function model.
    functionModels.set(
        functionData.function.name,
        new FunctionMetaData(
            filename,
            functionData.function.calls,
            functionData.function.declrator_id,
            functionData.function.start_line,
            functionData.function.end_line
        )
    );

    functionCount++;


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
    if (codeData.namespaces != null) {
        // Handle all namespaces
        codeData.namespaces.forEach((object) => {
           children.push(handleNamespaceData(object, filename));
        });
    }
    if (codeData.classes != null) {
        // Handle all classes
        codeData.classes.forEach((object, filename) => {
           children.push(handleClassData(object, filename));
        });
    }
    if (codeData.functions != null) {
        // Handle all functions
        codeData.functions.forEach((object) => {
           children.push(handleFunctionData(object, filename));
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
    lineCount = 0;

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
        if (file.file.parsed != true) {
            return;
        }

        // File is parsed correctly, process it.
        lineCount += file.file.linesInFile;
        var children = handleCodeData(file.file, file.file.file_name);

        children.forEach( function(child, index) {
            root.addChild(child);
            child.setParent(root);
        });

    });

    fdg.setTree(root);

    windowMgr.setFunctionCount(functionCount);
    windowMgr.setClassCount(classCount);
    windowMgr.setNamespaceCount(namespaceCount);
    windowMgr.setLineCount(lineCount);

    linkFunctionCalls();
}