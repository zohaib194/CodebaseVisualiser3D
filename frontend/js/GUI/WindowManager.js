// Configuration for imgui-js.
SystemJS.config({
  map: {
    "imgui-js": "/js/GUI/imgui-js"
  },
  packages: {
    "imgui-js": { main: "imgui.js" }
  }
});

// Global vars.
let ImGui;
let ImGui_Impl;


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


    let done = false;
    window.requestAnimationFrame(ImGuiLoop);

    /**
     * Loop for rendering imgui components.
     *
     * @class      ImGuiLoop (name)
     * @param      {float}  time    The time
     */
    function ImGuiLoop(time) {
      
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
        qualityMetricsWindow.initializeWindow();

        ImGui.EndFrame();

        ImGui.Render();



        ImGui_Impl.RenderDrawData(ImGui.GetDrawData());

        // TODO: restore WebGL state in ImGui Impl
        renderer.state.reset();

        window.requestAnimationFrame(done ? doneLoop : ImGuiLoop);
    }

    /**
     * Destroy imgui components.
     */
    function doneLoop() {
        ImGui_Impl.Shutdown();
        ImGui.DestroyContext();
    }
});