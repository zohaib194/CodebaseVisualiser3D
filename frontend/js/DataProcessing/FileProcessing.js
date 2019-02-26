var indexStack = new Array();
var functionModels = new Map();

// Number of classes found in the project
var classCount = 0;

// Number of functions found in the project.
var functionCount = 0;

// Number of namespaces found in the project.
var namespaceCount = 0;

/**
 * Function to get new random position.
 */
function randomPosition() {
    return new THREE.Vector3(
        // Random nr [0-19].
        Math.floor(Math.random() * 20),
        Math.floor(Math.random() * 20),
        Math.floor(Math.random() * 20)
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
 * Function for handling class data in JSONObject.
 * @param {JSONObject} classData - Data about a class.
 */
function handleClassData(classData) {
    classCount++;
    // Add node and save index.
    indexStack.push(
        fdg.addNode(
            new Node(
                randomPosition(), 
                classData.Class.name, 
                "class"
            )
        )
    );

    // If I have a parent, add link between us.
    linkElements();

    // Handle any children.
    handleCodeData(classData.Class);

    // We're done in this part of the tree.
    indexStack.pop();
}

/**
 * Function for handling namespace data in JSONObject.
 * @param {JSONObject} namespaceData - Data about namespace.
 */
function handleNamespaceData(namespaceData) {
    // Add node and save index to stack.
    indexStack.push(
        fdg.addNode(
            new Node(
                randomPosition(), 
                namespaceData.namespace.name, 
                "namespace"
            )
        )
    );

    // If I have a parent, add link between us.
    linkElements();

    namespaceCount++;

    // Handle any children.
    handleCodeData(namespaceData.namespace);

    // We're done in this part of the tree.
    indexStack.pop();
}

/**
 * Function for handling function data in JSONObject.
 * @param {JSONObject} functionData - Data about a function JSONObject.
 */
function handleFunctionData(functionData, fileName) {
    // Add node and save index to stack.
    indexStack.push(
        fdg.addNode(
            new Node(
                randomPosition(), 
                functionData.function.name, 
                "function"
            )
        )
    );

    // Save the function data in function model.
    functionModels.set(
        functionData.function.name,
        new FunctionMetaData( 
            fileName,
            functionData.function.start_line, 
            functionData.function.end_line
        )
    );

    // If I have a parent, add link between us.
    linkElements();

    functionCount++;

    // Handle any children.
    handleCodeData(functionData.function);

    // We're done in this part of the tree.
    indexStack.pop();
}

/**
 * Function for handling code data in JSONObject.
 * @param {JSONObject} codeData  - Data about general code.
 */
function handleCodeData(codeData) {
    if (codeData.namespaces != null) {
        // Handle all namespaces
        codeData.namespaces.forEach((object) => {
            handleNamespaceData(object);
        });
    } else if (codeData.classes != null) {
        // Handle all classes
        codeData.classes.forEach((object) => {
            handleClassData(object);
        });
    } else if (codeData.functions != null) {
        // Handle all functions
        codeData.functions.forEach((object) => {
            handleFunctionData(object, codeData.file_name);
        });
    }
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

    // Handle every file given.
    projectData.files.forEach((file) => {
        handleCodeData(file.file);        
    });

    windowMgr.setFunctionCount(functionCount);
    windowMgr.setClassCount(classCount);
    windowMgr.setNamespaceCount(namespaceCount);
}