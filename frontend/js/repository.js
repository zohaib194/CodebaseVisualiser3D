/**
 * Redirect to 3dwiew.html along with the repository name as query parameter.
 */
function submitRepoName(){
    repoName = document.getElementById("reponame");

    location.assign("./html/3DView.html?repo=" + repoName.value);

    // disable the form and enable a loader to the document.
    document.getElementById("repositoryform").style.display = "none";
}