SystemJS.config({
    map: {
        "imgui-js": "/imgui-js",
    	"js": "/js",
    },
    packages: {
        "imgui-js": { main: "imgui.js", },
        "js": {draw: "TestImgui.js", }
    }
});
