/**
 * Function makes repositories imgui window.
 *
 * @param      {<type>}  rootWindowSize  The root(menubar) window size
 * @param      {<type>}  rootWindowPos   The root(menubar) window position
 * @return     {Object}  object contains repository window related functions.
 */
let repositories = (function (rootWindowSize, rootWindowPos){
	var uri = new Array();

   	/**
   	 * Initialize repository window.
   	 */
    function init(){
        ImGui.SetNextWindowPos(new ImGui.ImVec2(rootWindowSize.x - 289, rootWindowSize.y), ImGui.Cond.Always);
        ImGui.SetNextWindowSize(new ImGui.ImVec2(290, canvas.height), ImGui.Cond.Always);
        ImGui.SetNextWindowCollapsed(false);
        ImGui.Begin("Repositories");
        this.wSize = ImGui.GetWindowSize();
        this.wPos = ImGui.GetWindowPos();
        for (var i = 0; i < uri.length; i++) {
        	ImGui.BulletText(uri[i]);
        }
        
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
     * Sets the uri array.
     *
     * @param      {array}  data    The data contains uris.
     */
    function setReposURI(data){
    	uri = data;
    }


    return {
        initializeWindow: init,
        getWindowPosition: getPosition,
        getWindowSize: getSize,
        setReposURI: setReposURI
    };
});