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
     * Function for making menubar object.
     *
     * @return     {Object}  object contains menubar functions.
     */
    let menubar = (function (){
        
        /**
         * Initialize menubar with fields.
         */
        function init(){
            if (ImGui.BeginMainMenuBar()) {
                this.wSize = ImGui.GetWindowSize();
                this.wPos = ImGui.GetWindowPos();

                if (ImGui.BeginMenu("File")) {
                    //ShowExampleMenuFile();
                    ImGui.EndMenu();
                }
                if (ImGui.BeginMenu("Edit")) {
                    if (ImGui.MenuItem("Undo", "CTRL+Z")) { }
                    if (ImGui.MenuItem("Redo", "CTRL+Y", false, false)) { } // Disabled item
                    ImGui.Separator();
                    if (ImGui.MenuItem("Cut", "CTRL+X")) { }
                    if (ImGui.MenuItem("Copy", "CTRL+C")) { }
                    if (ImGui.MenuItem("Paste", "CTRL+V")) { }
                    ImGui.EndMenu();
                }
                ImGui.EndMainMenuBar();
            } 
        }

        /**
         * Gets the position.
         *
         * @return     {ImGUI.ImVec2}  The position.
         */
        function getPosition(){
            return this.wPos;
        }

        /**
         * Gets the size.
         *
         * @return     {ImGUI.ImVec2}  The size.
         */
        function getSize(){
            return this.wSize;
        }

        return {
            initializeWindow: init,
            getWindowPosition: getPosition,
            getWindowSize: getSize,
        };
    })();
   
    /**
     * Function makes fileTree object.
     *
     * @param      {ImGUI.ImVec2}  previousWindowSize  The previous window size
     * @param      {ImGUI.ImVec2}  previousWindowPos   The previous window position
     * @return     {Object}  object contains fileTree functions.
     */
    let fileTree = (function (previousWindowSize, previousWindowPos){

        /**
         * Initialize fileTree window.
         */
        function init(){
            ImGui.SetNextWindowPos(new ImGui.ImVec2(0, previousWindowSize.y), ImGui.Cond.Always);
            ImGui.SetNextWindowSize(new ImGui.ImVec2(290, canvas.height/4), ImGui.Cond.Always);
            ImGui.SetNextWindowCollapsed(false);
            ImGui.Begin("File Tree");
            this.wSize = ImGui.GetWindowSize();
            this.wPos = ImGui.GetWindowPos();

            ImGui.End();
        }
        
        /**
         * Gets the position.
         *
         * @return     {ImGUI.ImVec2}  The position.
         */
        function getPosition(){
            return this.wPos;
        }

        /**
         * Gets the size.
         *
         * @return     {ImGUI.ImVec2}  The size.
         */
        function getSize(){
            return this.wSize;
        }

        return {
            initializeWindow: init,
            getWindowPosition: getPosition,
            getWindowSize: getSize,
        };
    });

    /**
     * Function makes namespace object.
     *
     * @param      {ImGUI.ImVec2}  previousWindowSize  The previous window size
     * @param      {ImGUI.ImVec2}  previousWindowPos   The previous window position
     * @return     {Object}  object contains namespace functions.
     */
    let namespace = (function (previousWindowSize, previousWindowPos){

        /**
         * Initialize namespace window.
         */
        function init(){
            ImGui.SetNextWindowPos(new ImGui.ImVec2(0, previousWindowSize.y + previousWindowPos.y), ImGui.Cond.Always);
            ImGui.SetNextWindowSize(new ImGui.ImVec2(290, canvas.height/4), ImGui.Cond.Always);
            ImGui.SetNextWindowCollapsed(false);
            ImGui.Begin("Namespace");
            this.wSize = ImGui.GetWindowSize();
            this.wPos = ImGui.GetWindowPos();


            ImGui.End();
        }
        
        /**
         * Gets the position.
         *
         * @return     {ImGUI.ImVec2}  The position.
         */
        function getPosition(){
            return this.wPos;
        }

        /**
         * Gets the size.
         *
         * @return     {ImGUI.ImVec2}  The size.
         */
        function getSize(){
            return this.wSize;
        }

        return {
            initializeWindow: init,
            getWindowPosition: getPosition,
            getWindowSize: getSize,
        };
    });

    /**
     *  Function makes codeInspection object.
     *
     * @param      {ImGUI.ImVec2}  previousWindowSize  The previous window size
     * @param      {ImGUI.ImVec2}  previousWindowPos   The previous window position
     * @return     {Object}   object contains codeInspection functions.
     */
    let codeInspection = (function (previousWindowSize, previousWindowPos){

        /**
         * Initialize code inspection window.
         */
        function init(){
            ImGui.SetNextWindowPos(new ImGui.ImVec2(0, previousWindowSize.y + previousWindowPos.y), ImGui.Cond.Always);
            ImGui.SetNextWindowSize(new ImGui.ImVec2(290, canvas.height/4), ImGui.Cond.Always);
            ImGui.SetNextWindowCollapsed(false);
            ImGui.Begin("Code Inspection");
            this.wSize = ImGui.GetWindowSize();
            this.wPos = ImGui.GetWindowPos();


            ImGui.End();
        }
        
        /**
         * Gets the position.
         *
         * @return     {ImGUI.ImVec2}  The position.
         */
        function getPosition(){
            return this.wPos;
        }

        /**
         * Gets the size.
         *
         * @return     {ImGUI.ImVec2} The size.
         */
        function getSize(){
            return this.wSize;
        }

        return {
            initializeWindow: init,
            getWindowPosition: getPosition,
            getWindowSize: getSize,
        };
    }); 

    /**
     * Function makes qualityMetrics object.
     *
     * @param      {ImGUI.ImVec2}  previousWindowSize  The previous window size
     * @param      {ImGUI.ImVec2}  previousWindowPos   The previous window position
     * @return     {Object}  object contains qualityMetrics functions.
     */
    let qualityMetrics = (function (previousWindowSize, previousWindowPos){
        /**
         * Initialize quality metrics window.
         */
        function init(){
            ImGui.SetNextWindowPos(new ImGui.ImVec2(0, previousWindowSize.y + previousWindowPos.y), ImGui.Cond.Always);
            ImGui.SetNextWindowSize(new ImGui.ImVec2(290, canvas.height/4), ImGui.Cond.Always);
            ImGui.SetNextWindowCollapsed(false);
            ImGui.Begin("Quality Metrics");
            this.wSize = ImGui.GetWindowSize();
            this.wPos = ImGui.GetWindowPos();


            ImGui.End();
        }
        
        /**
         * Gets the position.
         *
         * @return     {ImGUI.ImVec2}  The position.
         */
        function getPosition(){
            return this.wPos;
        }

        /**
         * Gets the size.
         *
         * @return     {ImGUI.ImVec2}  The size.
         */
        function getSize(){
            return this.wSize;
        }

        return {
            initializeWindow: init,
            getWindowPosition: getPosition,
            getWindowSize: getSize,
        };
    });

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