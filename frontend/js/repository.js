var repo;
console.log("repo");

function submitRepoName(){
    console.log("http://" + api_ip + ":" + api_port + "/repo/add");
	var repoName;

	repoName = document.getElementById("reponame");

	// Create a http request
	var xhr = new XMLHttpRequest();
	var body = 	{
					'uri': repoName.value
				};

	xhr.open("post", "http://" + api_ip + ":" + api_port + "/repo/add", true);

    xhr.onreadystatechange = function() {
	    if(xhr.readyState == 4 && xhr.status == 201) {
			repo = JSON.parse(xhr.responseText);
			location.assign("./html/3DView.html?id=" + repo.id);
	    }
    }
    console.log(JSON.stringify(body));
    xhr.send(JSON.stringify(body));

    // disable the form and enable a loader to the document.
   	document.getElementById("repositoryform").style.display = "none";
	document.getElementById("loader").style.display = "block";
}