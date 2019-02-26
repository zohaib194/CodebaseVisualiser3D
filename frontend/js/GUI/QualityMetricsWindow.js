/**
 * Function makes quality metrics imgui window.
 *
 * @param      {ImGUI.ImVec2}  previousWindowSize  The previous window size
 * @param      {ImGUI.ImVec2}  previousWindowPos   The previous window position
 * @return     {Object}  object contains qualityMetrics functions.
 */
let qualityMetrics = (function (previousWindowSize, previousWindowPos){

    var classCount = 0;

    /**
     * Initialize quality metrics window.
     */
    function init(){
        ImGui.SetNextWindowPos(new ImGui.ImVec2(0, previousWindowSize.y + previousWindowPos.y), ImGui.Cond.Always);
        ImGui.SetNextWindowSize(new ImGui.ImVec2(290, canvas.height/4), ImGui.Cond.Always);
        ImGui.SetNextWindowCollapsed(false);
        ImGui.Begin("Quality Metrics");
        ImGui.Text("Number of classes:      " + classCount);
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

    function setClassCount(count){
        classCount = count;
    }

    return {
        initializeWindow: init,
        setClassCount: setClassCount,
        getWindowPosition: getPosition,
        getWindowSize: getSize
    };
});