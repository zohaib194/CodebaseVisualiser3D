//Package controller refers to controll part of mvc.
//It performs validation, errorhandling and buisness logic
package controller

import (
	"encoding/json"
	"log"
	"net/http"
	"regexp"

	"github.com/gorilla/mux"
	"github.com/gorilla/websocket"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/model"
)

// RepoController represents metadata for a git repository.
type RepoController struct {
	URI string // Where the repository was found
}

// WebsocketResponse is the response format of a websocket
type WebsocketResponse struct {
	StatusCode int         `json:"statuscode"` // StatusCode is http equivalent of websocket status.
	StatusText string      `json:"statustext"` // StatusText is http equivalent of websocket status.
	Body       interface{} `json:"body"`       // Body is the content expected by the client.
}

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin: func(*http.Request) bool {
		return true
	},
}

/**
* @api {GET} /repo/add Add new git repository to server.
* @apiName Add repository.
* @apiGroup Repository
* @apiPermission none
*
* @apiDescription Expects a get request requesting a websocket upgrade.
* The following assumes a websocket has been established. On success
* the content will conatin a statuscode and statustext based on http status
* codes and a body with the repository id and creation status.
*
* @apiParam {String} URI URI to git repository.
*
* @apiParamExample {json} Add repository:
* 	{
*		uri: "git@github.com:zohaib194/CodebaseVisualizer3D.git"
*	}
*
* @apiSuccessExample {json} Status Cloning:
* 	WebSocket 1 TextMessage
*	{
*		"statuscode": 202
*		"statustext": Accepted
*		"body":{
*			"id": "5c62d1904122c760dafe9341"
*			"status": Cloning
*		}
*	}
*
* @apiSuccessExample {json} Status Done:
* 	WebSocket 8 CloseMessage 1000 CloseNormalClosure
*	{
*		"statuscode": 201
*		"statustext": Created
*		"body":{
*			"id": "5c62d1904122c760dafe9341"
*			"status": Done
*		}
*	}
*
* @apiErrorExample {Text} Post invalid git URI.
*	WebSocket 8 CloseMessage 1000 CloseNormalClosure
*
*		Expected URI to git repository
*
*
* @apiErrorExample {Text} Post invalid json.
*	WebSocket 8 CloseMessage 1000 CloseNormalClosure
*
*		Invalid json
*
 */

// NewRepoFromURI upgrades a getrequest to a websocket expecting the client to
// send a json with uri field and saves the git repository it refers to.
func (repo RepoController) NewRepoFromURI(w http.ResponseWriter, r *http.Request) {
	http.Header.Add(w.Header(), "content-type", "application/json")
	http.Header.Add(w.Header(), "Access-Control-Allow-Origin", "*")

	// Uses get to setup websocket
	if r.Method == "GET" {
		conn, err := upgrader.Upgrade(w, r, nil)
		if err != nil {
			http.Error(w, "Expecte to established WebSocket", http.StatusBadRequest)
			log.Println("Could not upgrade:", err)
			return
		}

		messageType, r, err := conn.NextReader()
		if err != nil {
			log.Println("Could not read request: ", err)
			return
		}

		if messageType != websocket.TextMessage {
			log.Println("Got unexpected websocket messageType")
			err := conn.WriteMessage(
				websocket.CloseMessage,
				websocket.FormatCloseMessage(
					websocket.CloseNormalClosure,
					"Expected TextMessage",
				),
			)
			if err != nil {
				log.Println("Could not write closer: ", err.Error())
				return
			}

			return
		}

		decoder := json.NewDecoder(r)
		var postData map[string]string

		if err := decoder.Decode(&postData); err != nil {
			log.Println("Could not decode json error: ", err.Error())
			err := conn.WriteMessage(
				websocket.CloseMessage,
				websocket.FormatCloseMessage(
					websocket.CloseNormalClosure,
					"Invalid message",
				),
			)
			if err != nil {
				log.Println("Could not write closer: ", err.Error())
				return
			}
			return
		}

		// Check that valid uri is given and that it is a .git
		if isValid, err := validateURI(postData["uri"],
			func(url string) (isValid bool, err error) { return regexp.Match(`\.git$`, []byte(postData["uri"])) }); !isValid || (err != nil) {
			log.Println("Not a valid URI to git repository.")
			reason := WebsocketResponse{
				StatusText: http.StatusText(http.StatusBadRequest),
				StatusCode: http.StatusBadRequest,
				Body: map[string]string{
					"id":     "",
					"status": "Expected URI to git repository",
				},
			}
			jsonResponse, err := json.Marshal(reason)
			if err != nil {
				log.Println("Could not encode json")
				return
			}
			err = conn.WriteMessage(
				websocket.CloseMessage,
				websocket.FormatCloseMessage(
					websocket.CloseNormalClosure,
					string(jsonResponse),
				),
			)
			if err != nil {
				log.Println("Could not write closer: ", err.Error())
				return
			}
			return
		}
		repo.URI = postData["uri"]

		// Setting up channel and go routine to save the new repo in database and on file
		saverChannel := make(chan model.SaveResponse)
		go model.RepoModel{URI: repo.URI}.Save(saverChannel)

		// Expecting response of save to contain save status and potential error.
		saverResponse := <-saverChannel

		// For each new message from save()
		for {

			if saverResponse.Err != nil {
				if saverResponse.Err.Error() == "Already exists" {
					log.Println("Request conflict with existing repository")
					reason := WebsocketResponse{
						StatusText: http.StatusText(http.StatusConflict),
						StatusCode: http.StatusConflict,
						Body: map[string]string{
							"id":     saverResponse.ID,
							"status": "Repository already exists",
						},
					}
					jsonResponse, err := json.Marshal(reason)
					if err != nil {
						log.Println("Could not encode json")
						return
					}
					err = conn.WriteMessage(
						websocket.CloseMessage,
						websocket.FormatCloseMessage(
							websocket.CloseNormalClosure,
							string(jsonResponse),
						),
					)
					if err != nil {
						log.Println("Could not write closer: ", err.Error())
						return
					}
					return

				}
				reason := WebsocketResponse{
					StatusText: http.StatusText(http.StatusConflict),
					StatusCode: http.StatusConflict,
					Body: map[string]string{
						"id":     saverResponse.ID,
						"status": "Database error",
					},
				}
				jsonResponse, err := json.Marshal(reason)
				if err != nil {
					log.Println("Could not encode json")
					return
				}
				err = conn.WriteMessage(
					websocket.CloseMessage,
					websocket.FormatCloseMessage(
						websocket.CloseNormalClosure,
						string(jsonResponse),
					),
				)
				if err != nil {
					log.Println("Error creating repository: ", err.Error())
					return
				}
				return
			}

			if saverResponse.StatusText == "Cloning" {
				response := WebsocketResponse{
					StatusText: http.StatusText(http.StatusAccepted),
					StatusCode: http.StatusAccepted,
					Body: map[string]string{
						"id":     saverResponse.ID,
						"status": saverResponse.StatusText,
					},
				}

				err = conn.WriteJSON(response)
				if err != nil {
					log.Println("Could not write message: ", err.Error())
					return
				}
			} else if saverResponse.StatusText == "Done" {
				response := WebsocketResponse{
					StatusText: http.StatusText(http.StatusCreated),
					StatusCode: http.StatusCreated,
					Body: map[string]string{
						"id":     saverResponse.ID,
						"status": saverResponse.StatusText,
					},
				}

				jsonResponse, err := json.Marshal(response)
				if err != nil {
					log.Println("Could not encode json")
					return
				}

				err = conn.WriteMessage(
					websocket.CloseMessage,
					websocket.FormatCloseMessage(
						websocket.CloseNormalClosure,
						string(jsonResponse),
					),
				)
				if err != nil {
					log.Println("Could not write closer: ", err.Error())
					return
				}

				return
			}

			saverResponse = <-saverChannel
		}

	} else { // if not Get request
		http.Error(w, http.StatusText(http.StatusMethodNotAllowed), http.StatusMethodNotAllowed)
		log.Println("Unsuported method", r.Method)
		return
	}
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
		files, err := exstRepo.GetRepoFiles()

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

	} else { // if not GET request
		http.Error(w, http.StatusText(http.StatusMethodNotAllowed), http.StatusMethodNotAllowed)
		return
	}

}

/**
* @api {Get} /repo/list Get all git repository stored in server.
* @apiName Get Repositories List.
* @apiGroup Repository
* @apiPermission none
*
*
* @apiSuccessExample {json} Success-Response:
* 	HTTP/1.1 200 OK
*	[
*	    {
*	        "_id": "5c768dae4122c7135145a1a3",
*	        "uri": "https://github.com/<USER_NAME>/Game_InputHandlingSystem.git"
*	    },
*	    {
*	        "_id": "5c768cf64122c7135145a1a2",
*	        "uri": "https://github.com/<USER_NAME>/imgui.git"
*	    },
*	    {
*	        "_id": "5c7684364122c703a493e292",
*	        "uri": "https://github.com/<USER_NAME>/ECS.git"
*	    }
*	]
*
*
* @apiErrorExample {text/plain} Invalid method.
*	HTTP/1.1 405 Method Not Allowed
*	{
*		Method Not Allowed
*	}
 */

// GetAllRepos gets all repositories stored.
func (repo RepoController) GetAllRepos(w http.ResponseWriter, r *http.Request) {
	http.Header.Add(w.Header(), "content-type", "application/json")
	http.Header.Add(w.Header(), "Access-Control-Allow-Origin", "*")

	if r.Method == "GET" {
		repos, err := model.RepoModel{}.FetchAll()

		if err != nil {
			http.Error(w, http.StatusText(http.StatusInternalServerError), http.StatusInternalServerError)
			log.Println("Could not find repositories error: ", err.Error())
			return
		}

		w.WriteHeader(http.StatusOK)
		json.NewEncoder(w).Encode(repos)

	} else { // if not GET request
		http.Error(w, http.StatusText(http.StatusMethodNotAllowed), http.StatusMethodNotAllowed)
		return
	}

}
