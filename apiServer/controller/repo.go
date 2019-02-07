//controller refers to controll part of mvc.
//It performs validation, errorhandling and buisness logic
package controller

import (
	"encoding/json"
	"hash"
	"log"
	"net/http"
	"regexp"
)

// Repo represents metadata for a git repository.
type Repo struct {
	uri  string    // Where the repository was found
	name string    // The name of the repository
	id   hash.Hash // Identifier for the repository
}

	/**
	* @api {Post} /repo/add Add new git repository to server.
	* @apiName Add repository.
	* @apiGroup Repository
	* @apiPermission none
	* 
	* 
	* @apiParam {String} URI URI to git repository.
	* 
	* @apiParamExample {json} Add repository:
	* 	{
	*		uri: "git@github.com:zohaib194/CodebaseVisualizer3D.git"
	*	}
	* 
	* @apiErrorExample {json} Post invalid git URI.
	*	HTTP/1.1 400 Bad Request
	*	{
	*		Expected URI to git repository
	*	}
	*
	* @apiErrorExample {json} Post invalid json.
	*	HTTP/1.1 400 Bad Request
	*	{
	*		Invalid json
	*	}
	*/

// NewRepoFromURI takes json request with uri field and stores the git repository it refers to.
func (repo Repo) NewRepoFromURI(w http.ResponseWriter, r *http.Request) {
	http.Header.Add(w.Header(), "content-type", "application/json")

	if r.Method == "POST" {

		decoder := json.NewDecoder(r.Body)
		var postData map[string]string

		if err := decoder.Decode(&postData); err != nil {
			http.Error(w, "Invalid json", http.StatusBadRequest)
			log.Println("Could not decode json error: ", err.Error())
			return
		}

		// Check that valid uri is given and that it is a .git
		if isValid, err := validateURI(postData["uri"],
			func(url string) (isValid bool, err error) { return regexp.Match(`\.git$`, []byte(postData["uri"])) }); !isValid || (err != nil) {

			http.Error(w, "Expected URI to git repository", http.StatusBadRequest)
			log.Println("Not a valid URI to git repository.")
			return
		}

	} else { // if not POST request
		http.Error(w, http.StatusText(http.StatusBadRequest), http.StatusBadRequest)
		return
	}

	return
}
