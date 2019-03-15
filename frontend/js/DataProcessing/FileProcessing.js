var indexStack = new Array();
var functionModels = new Map();

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
 * Function for handling class data in JSONObject.
 * @param {JSONObject} classData - Data about a class.
 */
function handleClassData(classData, filename, scope) {
    // Handle any children.
    innerGeometry = handleCodeData(classData.Class, filename);
    position = randomPosition(innerGeometry.position, innerGeometry.size)
    classCount++;
    // Add node and save index.
    nodeSelf = scope.addNode(
        new Node(
            position,
            classData.Class.name,
            innerGeometry.size,
            "class"
        )
    );


    while
    linkElements();


    // We're done in this part of the tree.
    indexStack.pop();

    return {position: position, size: innerGeometry.size + 10};
}

/**
 * Function for handling namespace data in JSONObject.
 * @param {JSONObject} namespaceData - Data about namespace.
 */
function handleNamespaceData(namespaceData, filename) {
    // Handle any children.
    innerGeometry = handleCodeData(namespaceData.namespace, filename);
    position = randomPosition(innerGeometry.position, innerGeometry.size);

    // Add node and save index to stack.
    indexStack.push(
        fdg.addNode(
            new Node(
                position,
                namespaceData.namespace.name,
                innerGeometry.size,
                "namespace"
            )
        )
    );

    // If I have a parent, add link between us.
    linkElements();

    namespaceCount++;


    // We're done in this part of the tree.
    node = indexStack.pop();
    return {position: position, size: innerGeometry.size + 10};
}

/**
 * Function for handling function data in JSONObject.
 * @param {JSONObject} functionData - Data about a function JSONObject.
 */
function handleFunctionData(functionData, filename) {
    // Handle any children.
    innerGeometry = handleCodeData(functionData.function, filename);
    position = randomPosition(innerGeometry.position, innerGeometry.size);

    // Add node and save index to stack.
    indexStack.push(
        fdg.addNode(
            new Node(
                position,
                functionData.function.name,
                innerGeometry.size,
                "function"
            )
        )
    );
    // Save the function data in function model.
    functionModels.set(
        functionData.function.name,
        new FunctionMetaData(
            filename,
            functionData.function.start_line,
            functionData.function.end_line
        )
    );

    // If I have a parent, add link between us.
    linkElements();

    functionCount++;


    // We're done in this part of the tree.
    indexStack.pop();
    return {position: position, size: innerGeometry.size + 10, };
}

/**
 * Function for handling code data in JSONObject.
 * @param {JSONObject} codeData  - Data about general code.
 */
function handleCodeData(codeData, filename, scope) {
    scope = new FDG(1, 1, 0.1, 10, new THREE.Vector3(0, 0, 0));         // force directed graph for subtree
    innerGeometry = {position: new THREE.Vector3(0,0,0), size: 10}      // Inner geometry of subtree

    if (codeData.namespaces != null) {
        // Handle all namespaces
        codeData.namespaces.forEach((object) => {
           geometry = handleNamespaceData(object, filename, innerFdg);
           innerGeometry.size += geometry.size;
        });
    }
    if (codeData.classes != null) {
        // Handle all classes
        codeData.classes.forEach((object, filename) => {
           geometry = handleClassData(object, filename, innerFdg);
           innerGeometry.size += geometry.size;
        });
    }
    if (codeData.functions != null) {
        // Handle all functions
        codeData.functions.forEach((object) => {
           geometry = handleFunctionData(object, filename, innerFdg);
           innerGeometry.size += geometry.size;
        });
    }

    return {geometry: innerGeometry, scope: scope};
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
    // Handle every file given.
    projectData.files.forEach((file) => {

        // If file is not parsed, skip it
        // ("return" returns from lambda not forEach).
        if (file.file.parsed != true) {
            return;
        }

        // File is parsed correctly, process it.
        lineCount += file.file.linesInFile;
        handleCodeData(file.file, file.file.file_name);
    });

    windowMgr.setFunctionCount(functionCount);
    windowMgr.setClassCount(classCount);
    windowMgr.setNamespaceCount(namespaceCount);
    windowMgr.setLineCount(lineCount);
}