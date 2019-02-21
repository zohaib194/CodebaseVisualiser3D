//Package controller refers to controll part of mvc.
//It performs validation, errorhandling and buisness logic
package controller

import (
	"encoding/json"
	"log"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/model"
	"gopkg.in/mgo.v2/bson"
)

// FileController represents metadata for a file from a git project.
type CodeSnippetController struct {
}

/**
* @api {GET} http://localhost:8080/repo/:repoId/file/read/?lineStart=:StartNr&lineEnd=:EndNr&filePath=:filePath Fetch the implementation of file based upon range.
* @apiName Get Implementation.
* @apiGroup File
* @apiPermission none
*
* @apiParam {String} repoId Id of submitted git repository.
* @apiParam {Int} StartNr line number where the fetch start.
* @apiParam {Int} EndNr line number where the fetch stop.
* @apiParam {filePath} filePath is the file to fetch from.
*
 *
* @apiParamExample {url} Parse repository:
*     {
*     	repoId: 5c62d1904122c760dafe9341
*       lineStart: 17
*		lineEnd: 28
*		filePath: 5c62d1904122c760dafe9341/main.cpp
*     }
*
* @apiSuccessExample {json} Success-Response:
* 	HTTP/1.1 200 OK
*	{
*		"implementation": "\t\t{\n\t\t\tstd::string name = typeid(*this).name();\n\t\t\tif (typeid(*this).__is_pointer_p())\n\t\t\t\tname.erase(name.begin(), name.begin() + 1);\n\t\t\tif (removeDigits)\n\t\t\t{\n\t\t\t\tint i = 0;\n\t\t\t\twhile (isdigit(name.at(i))) i++;\n\t\t\t\tname.erase(name.begin(), name.begin() + i);\n\t\t\t}\n\t\t\treturn name;\n\t\t}\n"
*	}
*
* @apiErrorExample {json} Invalid parameters.
*	HTTP/1.1 400 Bad Request
*	{
*		"Invalid url parameter 'lineStart'|'lineEnd'|'filePath'"
*	}
*
* @apiErrorExample {json} Internal error.
*	HTTP/1.1 500 Internal Server Error
*	{
*		Internal Server Error
*	}
*
*/

// GetImplementation fetch implementation of data structure based upon query parameters.
func (codeSnippet CodeSnippetController) GetImplementation(w http.ResponseWriter, r *http.Request) {
	http.Header.Add(w.Header(), "content-type", "application/json")
	http.Header.Add(w.Header(), "Access-Control-Allow-Origin", "*")

	if r.Method == "GET" {
		vars := mux.Vars(r)

		// Get lineStart parameter from url.
		lineStart, ok := r.URL.Query()["lineStart"]
		if !ok || len(lineStart[0]) < 1 {
			http.Error(w, "Invalid url parameter 'lineStart'", http.StatusBadRequest)
			log.Println("Url parameter 'lineStart' is missing")
			return
		}

		// Get lineEnd parameter from url.
		lineEnd, ok := r.URL.Query()["lineEnd"]
		if !ok || len(lineEnd[0]) < 1 {
			http.Error(w, "Invalid url parameter 'lineEnd'", http.StatusBadRequest)
			log.Println("Url parameter 'lineEnd' is missing")
			return
		}

		// Get filePath parameter from url.
		filePath, ok := r.URL.Query()["filePath"]
		if !ok || len(filePath[0]) < 1 {
			http.Error(w, "Invalid url parameter 'filePath'", http.StatusBadRequest)
			log.Println("Url parameter 'filePath' is missing")
			return
		}

		// Convert strings to integers.
		startLine, err := strconv.Atoi(lineStart[0])
		if err != nil {
			http.Error(w, http.StatusText(http.StatusInternalServerError), http.StatusInternalServerError)
			log.Println("Could not convert startLine to integer", err.Error())
		}

		endLine, err := strconv.Atoi(lineEnd[0])
		if err != nil {
			http.Error(w, http.StatusText(http.StatusInternalServerError), http.StatusInternalServerError)
			log.Println("Could not convert endLine to integer", err.Error())
		}

		// Fetch the content of file.
		implementation, err := model.CodeSnippetModel{
			FilePath:  filePath[0],
			ID:        bson.ObjectIdHex(vars["repoId"]),
			StartLine: startLine,
			EndLine:   endLine,
		}.FetchLinesOfCode()

		if err != nil {
			http.Error(w, http.StatusText(http.StatusInternalServerError), http.StatusInternalServerError)
			log.Println("Error occurred during FetchLinesOfCode", err.Error())
		}

		codeMap := make(map[string]string)
		codeMap["implementation"] = implementation

		w.WriteHeader(http.StatusOK)
		json.NewEncoder(w).Encode(codeMap)

	} else { // if not POST request
		http.Error(w, http.StatusText(http.StatusBadRequest), http.StatusBadRequest)
		return
	}

}
