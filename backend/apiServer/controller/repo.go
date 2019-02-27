//Package controller refers to controll part of mvc.
//It performs validation, errorhandling and buisness logic
package controller

import (
	"encoding/json"
	"log"
	"net/http"
	"regexp"

	"github.com/gorilla/mux"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/model"
)

// RepoController represents metadata for a git repository.
type RepoController struct {
	URI string // Where the repository was found
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
* @apiSuccessExample {json} Success-Response:
* 	HTTP/1.1 201 Created
*	{
*		"id": "5c62d1904122c760dafe9341"
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
func (repo RepoController) NewRepoFromURI(w http.ResponseWriter, r *http.Request) {
	http.Header.Add(w.Header(), "content-type", "application/json")
	http.Header.Add(w.Header(), "Access-Control-Allow-Origin", "*")
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
		repo.URI = postData["uri"]
		id, err := model.RepoModel{URI: repo.URI}.Save()

		if err != nil {
			if err.Error() == "Already exists" {
				http.Error(w, "Repository already exists", http.StatusConflict)
				return

			}

			http.Error(w, "Database error", http.StatusInternalServerError)
			return
		}

		ID := map[string]string{"id": id}

		w.WriteHeader(http.StatusCreated)
		json.NewEncoder(w).Encode(ID)

	} else { // if not POST request
		http.Error(w, http.StatusText(http.StatusMethodNotAllowed), http.StatusMethodNotAllowed)
		return
	}

	return
}

/**
* @api {GET} /repo/:id/initial/ Parse the repository assosiated with id.
* @apiName Parse repository.
* @apiGroup Repository
* @apiPermission none
*
* @apiParam {String} Id Id of submitted git repository.
*
* @apiParamExample {url} Parse repository:
*     {
*       "id": 5c62d1904122c760dafe9341
*     }
*
* @apiSuccessExample {json} Success-Response:
* 	HTTP/1.1 200 OK
*	{
*	    "functions": [
*	        {
*	            "file": "main.cpp",
*	            "function_names": [
*	                {
*	                    "name": "int main()"
*	                }
*	            ]
*	        }
*	    ]
*	}
*
* @apiErrorExample {json} Post invalid id.
*	HTTP/1.1 400 Bad Request
*	{
*		Invalid parameters
*	}
*
* @apiErrorExample {json} Internal error.
*	HTTP/1.1 500 Internal Server Error
*	{
*		Internal Server Error
*	}
*
 */

// ParseInitial parse a repository for functions of a certain project in repos directory.
func (repo RepoController) ParseInitial(w http.ResponseWriter, r *http.Request) {
	http.Header.Add(w.Header(), "content-type", "application/json")
	http.Header.Add(w.Header(), "Access-Control-Allow-Origin", "*")

	if r.Method == "GET" {
		vars := mux.Vars(r)

		// Validate that the project exist in DB.
		exstRepo, err := model.RepoModel{}.GetRepoByID(vars["repoId"])

		if err != nil {
			http.Error(w, "Invalid parameters", http.StatusBadRequest)
			log.Println("Could not find repo in db: ", err.Error())
			return
		}

		// List all files in the repository directory.
		files, err := exstRepo.GetRepoFile()

		if err != nil {
			http.Error(w, http.StatusText(http.StatusInternalServerError), http.StatusInternalServerError)
			log.Println("Could not find files error: ", err.Error())
			return
		}

		// Fetch all fuctions given in files.
		projectModel, err := exstRepo.ParseDataFromFiles(files)

		if err != nil {
			http.Error(w, http.StatusText(http.StatusInternalServerError), http.StatusInternalServerError)
			log.Println("Could not parse error: ", err.Error())
			return
		}

		w.WriteHeader(http.StatusOK)
		json.NewEncoder(w).Encode(projectModel)

	} else { // if not POST request
		http.Error(w, http.StatusText(http.StatusBadRequest), http.StatusBadRequest)
		return
	}

}
