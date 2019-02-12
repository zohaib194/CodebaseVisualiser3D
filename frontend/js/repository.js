function createCORSRequest(method, url){
    var xhr = new XMLHttpRequest();
    if ("withCredentials" in xhr){
        xhr.open(method, url, true);
    } else if (typeof XDomainRequest != "undefined"){
        xhr = new XDomainRequest();
        xhr.open(method, url);
    } else {
        xhr = null;
    }
    return xhr;
}

function submitRepoName(){
	var repoName;

	repoName = document.getElementById("reponame")
	

	console.log(repoName.value)


	var xhr = createCORSRequest("post", "http://localhost:8080/repo/add");
	var body = 	{
					'uri': repoName.value
			   	}

    xhr.onreadystatechange = xhr.onreadystatechange = function() {	
	    if(xhr.readyState == 4 && xhr.status == 200) {
	        alert(xhr.responseText);
	    }
	}
    xhr.send(JSON.stringify(body));

    /*
	var url = 'http://localhost:8080/repo/add';
	
	xhr.open('POST', url, true);

	//Send the proper header information along with the request.
	xhr.setRequestHeader("Content-Type", "application/json");

	//Call a function when the state changes.
	xhr.onreadystatechange = function() {	
	    if(xhr.readyState == 4 && xhr.status == 200) {
	        alert(xhr.responseText);
	    }
	}
	xhr.send(JSON.stringify(body));*/
	
}