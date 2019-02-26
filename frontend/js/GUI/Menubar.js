/**
 * Function for making imgui menubar.
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