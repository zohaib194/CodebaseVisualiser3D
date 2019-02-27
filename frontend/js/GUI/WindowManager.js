var ImGui;
var ImGui_Impl;

var WindowManager = (function(){
    // Vars.

    var classCount = 0;
    var implementation = "";

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

        if(ImGui_Impl == null){
            console.log("ImGui_Impl is null");
        }

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
        codeInspectionWindow.setImplementation(implementation);
        codeInspectionWindow.initializeWindow();

        // Quality metrics window.
        qualityMetricsWindow = qualityMetrics(codeInspectionWindow.getWindowSize(), codeInspectionWindow.getWindowPosition())
        qualityMetricsWindow.setClassCount(classCount);
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

    function setClassCount(count){
       classCount = count;
    }

    function setImplementationInCodeInspection(data){
        implementation = data;
    }

    return {
        ImGuiUpdate: ImGuiUpdate,
        ImGuiRender: ImGuiRender,
        ImGuiDestroy: ImGuiDestroy,
        setClassCount: setClassCount,
        setImplementationInCodeInspection: setImplementationInCodeInspection
    };
});
