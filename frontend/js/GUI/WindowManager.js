// Vars.
let ImGui;
let ImGui_Impl;

var WindowManager = (function(){

    Promise.resolve().then(() => {
        return System.import("imgui-js").then((module) => {
            ImGui = module;
            return ImGui.default();
        });
    }).then(() => {
        return System.import("imgui-js/example/imgui_impl").then((module) => {
            ImGui_Impl = module;
        });
    }).then(() => {
        const canvas = document.getElementById("output");

        ImGui.CreateContext();
        ImGui_Impl.Init(canvas);

        ImGui.StyleColorsDark();
        //ImGui.StyleColorsClassic();
    }).catch((error) => {
        console.log("Error: " + error);
    });

    var functionCount = 0;

    /**
     * Loop for rendering imgui components.
     *
     * @param      {float}  time    The time
     */
    function ImGuiUpdate(time) {
        ImGui_Impl.NewFrame(time);
        ImGui.NewFrame();
        
        // menu bar of the program.        
        menubar.initializeWindow();

        // File tree window.
        fileTreeWindow = fileTree(menubar.getWindowSize(), menubar.getWindowPosition())
        fileTreeWindow.initializeWindow();

        // Namespace window.
        namespaceWindow = namespace(fileTreeWindow.getWindowSize(), fileTreeWindow.getWindowPosition())
        namespaceWindow.initializeWindow();

        // Code Inpection window.
        codeInspectionWindow = codeInspection(namespaceWindow.getWindowSize(), namespaceWindow.getWindowPosition())
        codeInspectionWindow.initializeWindow();
        
        // Quality metrics window.
        qualityMetricsWindow = qualityMetrics(codeInspectionWindow.getWindowSize(), codeInspectionWindow.getWindowPosition())
        qualityMetricsWindow.setFunctionCount(functionCount);
        qualityMetricsWindow.initializeWindow();

        ImGui.EndFrame();
    }

    /**
     * Render imgui components and restore gl data.
     *
     */
    function ImGuiRender(){
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

    return {
        ImGuiUpdate: ImGuiUpdate,
        ImGuiRender: ImGuiRender,
        ImGuiDestroy: ImGuiDestroy,
        setFunctionCount: setFunctionCount
    };
});
