var repo;

function submitRepoName(){
	var repoName;

	repoName = document.getElementById("reponame");

	// Create a http request
	var xhr = new XMLHttpRequest();
	var body = 	{
					'uri': repoName.value
				};

	xhr.open("post", "http://localhost:8080/repo/add", true);

    xhr.onreadystatechange = function() {
	    if(xhr.readyState == 4 && xhr.status == 201) {
			repo = JSON.parse(xhr.responseText);
			window.location.assign("./html/3DView.html?id=" + repo.id);
	    }
	}
    xhr.send(JSON.stringify(body));

    // disable the form and enable a loader to the document.
   	document.getElementById("repositoryform").style.display = "none";
	document.getElementById("loader").style.display = "block";
}