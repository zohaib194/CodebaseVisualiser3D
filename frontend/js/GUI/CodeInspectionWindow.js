/**
 *  Function makes code inspection imgui window.
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