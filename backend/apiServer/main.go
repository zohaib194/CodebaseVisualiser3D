//main deals with environment and api definition.
package main

import (
	"github.com/gorilla/mux"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/controller"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/model"
	"log"
	"net/http"
	"os"
)

// Indicate the importance of a warning
const (
	logWarn  = "[WARNING]: "
	logError = "[ERROR]: "
	logInfo  = "[INFO]: "
)

func main() {
	r := mux.NewRouter()

	// Get environment variables
	port := os.Getenv("PORT")
	model.RepoPath = os.Getenv("REPOSITORY_PATH")
	dbLocation := os.Getenv("DB_LOCATION")
	model.JavaParserPath = os.Getenv("JAVA_PARSER")

	// Validate variables
	if len(port) == 0 {
		log.Fatal(logError + "$PORT was not set")
	}
	if len(model.RepoPath) == 0 {
		log.Fatal(logError + "$REPOSITORY_PATH was not set")
	}
	if len(dbLocation) == 0 {
		log.Fatal(logError + "$DB_LOCATION was not set")
	}
	if len(model.JavaParserPath) == 0 {
		log.Fatal(logError + "$JAVA_PARSER was not set")
	}

	// Variable setup
	model.DB.DatabaseURL = dbLocation
	if err := model.DB.Init(); err != nil {
		log.Fatal(logError + "Could not initialize database")
	}

	// API routings
	r.HandleFunc("/repo/add", controller.RepoController{}.NewRepoFromURI)
	r.HandleFunc("/repo/{repoId}/initial/", controller.RepoController{}.ParseSimpleFunc)
	r.HandleFunc("/repo/{repoId}/file/read/", controller.FileController{}.GetImplementation)

	// Start server
	log.Printf("%s Listening on port: %v", logInfo, port)
	http.ListenAndServe(":"+port, r)
}
