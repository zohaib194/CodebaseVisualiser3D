// Vars.
let ImGui;
let ImGui_Impl;

var WindowManager = (function(){

    var classCount = 0;
    var functionCount = 0;
    var namespaceCount = 0;
    var variableCount = 0;
    var lineCount = 0;
    var implementation = "";
    var repos = new Array();

    Promise.resolve().then(() => {
        return System.import("imgui-js").then((module) => {
            ImGui = module;
            return ImGui.default();
        });
    }).then(() => {
        return System.import("imgui-js/example/imgui_impl.js").then((module) => {
            ImGui_Impl = module;
        });
    }).then(() => {
        const canvas = document.getElementById("output");

        ImGui.CreateContext();
        ImGui_Impl.Init(canvas);

        ImGui.StyleColorsDark();
        //ImGui.StyleColorsClassic();
    }).catch((error) => {
        console.log(error);
    });


    /**
     * Loop for rendering imgui components.
     *
     * @param      {float}  time    The time
     */
    function ImGuiUpdate(time) {
        if (typeof ImGui_Impl === "undefined" || typeof ImGui === "undefined") {
            return ;
        }

        ImGui_Impl.NewFrame(time);
        ImGui.NewFrame();

        // menu bar of the program.
        menubar.initializeWindow();

        // File tree window.
        fileTreeWindow = fileTree(menubar.getWindowSize(), menubar.getWindowPosition());
        fileTreeWindow.initializeWindow();

        // Namespace window.
        namespaceWindow = namespace(fileTreeWindow.getWindowSize(), fileTreeWindow.getWindowPosition());
        namespaceWindow.initializeWindow();

        // Code Inpection window.
        codeInspectionWindow = codeInspection(namespaceWindow.getWindowSize(), namespaceWindow.getWindowPosition());
        codeInspectionWindow.setImplementation(implementation);
        codeInspectionWindow.initializeWindow();

        // Quality metrics window.
        qualityMetricsWindow = qualityMetrics(codeInspectionWindow.getWindowSize(), codeInspectionWindow.getWindowPosition());
        qualityMetricsWindow.setFunctionCount(functionCount);
        qualityMetricsWindow.setClassCount(classCount);
        qualityMetricsWindow.setNamespaceCount(namespaceCount);
        qualityMetricsWindow.setVariableCount(variableCount);
        qualityMetricsWindow.setLineCount(lineCount);
        qualityMetricsWindow.initializeWindow();

        // Repository window.
        repositoriesWindow = repositories(menubar.getWindowSize(), menubar.getWindowPosition());
        repositoriesWindow.setReposURI(repos);
        repositoriesWindow.initializeWindow();

        ImGui.EndFrame();
    }

    /**
     * Render imgui components and restore gl data.
     *
     */
    function ImGuiRender(){
        if (typeof ImGui_Impl === "undefined" || typeof ImGui === "undefined") {
            return ;
        }

        ImGui.Render();
        ImGui_Impl.RenderDrawData(ImGui.GetDrawData());
        renderer.state.reset();
    }

    /**
     * Destroy imgui components.
     */
    function ImGuiDestroy() {
        ImGui_Impl.Shutdown();
        ImGui.DestroyContext();
    }

    /**
     * Setter the amount of functions.
     * @param {int} count - Amount of functions in program.
     */
    function setFunctionCount(count) {
        functionCount = count;
    }

    /**
     * Setter the amount of classes.
     * @param {int} count - Amount of classes in program.
     */
    function setClassCount(count){
       classCount = count;
    }

    /**
     * Setter the amount of namespaces.
     * @param {int} count - Amount of namespaces in program.
     */
    function setNamespaceCount(count){
       namespaceCount = count;
    }

    /**
     * Sets the variable count.
     * @param      {int}  count   The count
     */
    function setVariableCount(count){
       variableCount = count;
    }

    /**
     * Sets the implementation in code inspection.
     *
     * @param      {string}  data    The multiline string of implementation.
     */
    function setDataStructureImplementation(data){
        implementation = data;
    }

    /**
     * Sets the repositories.
     *
     * @param      {array}  data    The data is array of repository models.
     */
    function setRepositories(data){
        data.forEach((repo, index) =>  {
            repos[index] = repo.uri;
        })
    }

    /**
     * Sets the number of lines in project.
     *
     * @param      {int}  count   Number of lines found
     */
    function setLineCount(count){
        lineCount = count
    }

    return {
        ImGuiUpdate: ImGuiUpdate,
        ImGuiRender: ImGuiRender,
        ImGuiDestroy: ImGuiDestroy,
        setFunctionCount: setFunctionCount,
        setClassCount: setClassCount,
        setNamespaceCount: setNamespaceCount,
        setVariableCount: setVariableCount,
        setDataStructureImplementation: setDataStructureImplementation,
        setRepositories: setRepositories,
        setLineCount: setLineCount,
    };
});
