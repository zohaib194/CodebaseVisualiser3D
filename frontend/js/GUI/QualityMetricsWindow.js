
/**
 * Function makes quality metrics imgui window.
 *
 * @param      {ImGUI.ImVec2}  previousWindowSize  The previous window size
 * @param      {ImGUI.ImVec2}  previousWindowPos   The previous window position
 * @return     {Object}  object contains qualityMetrics functions.
 */
let qualityMetrics = (function (previousWindowSize, previousWindowPos){
    
    var functionCount = 0;
    var classCount = 0;
    var namespaceCount = 0;
    var lineCount

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
        
        ImGui.Text(
            LOCALE.getSentence("quality_metric_namespace_count") + ": " + namespaceCount
        );
        ImGui.Text(
            LOCALE.getSentence("quality_metric_class_count") + ": " + classCount
        );
        ImGui.Text(
            LOCALE.getSentence("quality_metric_funciton_count") + ": " + functionCount
        );
        ImGui.Text(
            LOCALE.getSentence("quality_metric_lines_count") + ": " + lineCount
        );

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

    /**
     * Setter of the amount of functions.
     * @param {int} count - Amount of functions in program.
     */
    function setFunctionCount(count) {
        if (count === "undefined") {
            console.log(LOCALE.getSentence("generic_undefined"));
            return;
        } else if (count < 0) {
            console.log(LOCALE.getSentence("generic_negative"));
            return;
        }

        functionCount = count;
    }

    /**
     * Setter of the amount of classes.
     * @param {int} count - Amount of classes in program.
     */
    function setClassCount(count){
        if (count === "undefined") {
            console.log(LOCALE.getSentence("generic_undefined"));
            return;
        } else if (count < 0) {
            console.log(LOCALE.getSentence("generic_negative"));
            return;
        }

        classCount = count;
    }

    /**
     * Setter of the amount of namespaces.
     * @param {int} count - Amount of namespaces in program.
     */
    function setNamespaceCount(count){
        if (count === "undefined") {
            console.log(LOCALE.getSentence("generic_undefined"));
            return;
        } else if (count < 0) {
            console.log(LOCALE.getSentence("generic_negative"));
            return;
        }

        namespaceCount = count;
    }

    /**
     * Setter of the lines of code.
     * @param {int} count - Lines of code.
     */
    function setLineCount(count){
        if (count === "undefined") {
            console.log(LOCALE.getSentence("generic_undefined"));
            return;
        } else if (count < 0) {
            console.log(LOCALE.getSentence("generic_negative"));
            return;
        }

        lineCount = count;
    }

    return {
        initializeWindow: init,
        getWindowPosition: getPosition,
        getWindowSize: getSize,
        setFunctionCount: setFunctionCount,
        setClassCount: setClassCount,
        setNamespaceCount: setNamespaceCount,    
        setLineCount: setLineCount,
    };
});