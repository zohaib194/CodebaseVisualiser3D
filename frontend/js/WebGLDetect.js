window.addEventListener("load", function() {
    var canvas = document.createElement("canvas");
    canvas.style.visibility = "hidden";

    var gl = canvas.getContext("webgl");
    if (!gl)                                // No webgl context was found.
    {
        // Add notification bar as first element under body.
        var notificationbar = document.createElement("div");
        notificationbar.className = "notificationbar";
        document.body.insertBefore(notificationbar, document.body.firstChild);
        notificationbar.innerHTML = "You're browser doesn't support WebGL! " + 
            "Visit: <a href=\"https://get.webgl.org/\">WebGL support!</a>";
        notificationbar.style.visibility = "visible";
    }
}, false);