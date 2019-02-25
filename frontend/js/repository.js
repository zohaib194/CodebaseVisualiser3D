var repo;

function submitRepoName(){
	var repoName;

	repoName = document.getElementById("reponame");

	fetch("http://" + config.serverInfo.api_ip + ":" + config.serverInfo.api_port + "/repo/add", {
        method: "POST",
		body: JSON.stringify({
			"uri": repoName.value,
		})
    }).then((response) => {
        // Once ready and everything went ok.
        if (response.status == 200 || response.status == 201) {
            console.log("Got something, moving on!");
            return response.json();
        }

        console.log("Didn't receive anything!");
        // Something went wrong.
        return Promise.reject();
    }).then((json) => {
        console.log(json);
        // Redirect to 3DView.js.
        location.assign("./html/3DView.html?id=" + json.id);
    }).catch((error) => {
        console.log("Error: " + error);
    });

	// Create a http request
	/*var xhr = new XMLHttpRequest();
	var body = 	;

	xhr.open("post", , true);

    xhr.onreadystatechange = function() {
	    if(xhr.readyState == 4 && xhr.status == 201) {
			repo = JSON.parse(xhr.responseText);
			location.assign("./html/3DView.html?id=" + repo.id);
	    }
    }
		
    xhr.send(JSON.stringify(body));*/

    // disable the form and enable a loader to the document.
   	document.getElementById("repositoryform").style.display = "none";
	document.getElementById("loader").style.display = "block";
}