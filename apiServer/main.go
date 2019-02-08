//main deals with environment and api definition.
package main

import (
	"github.com/zohaib194/CodebaseVisualizer3D/apiServer/controller"
	"github.com/zohaib194/CodebaseVisualizer3D/apiServer/model"
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

	// Get environment variables
	port := os.Getenv("PORT")
	model.RepoPath = os.Getenv("REPOSITORY_PATH")
	dbLocation := os.Getenv("DB_LOCATION")

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

	// Variable setup
	model.DB.DatabaseURL = dbLocation
	if err := model.DB.Init(); err != nil {
		log.Fatal(logError + "Could not initialize database")
	}

	// API routings
	http.HandleFunc("/repo/add", controller.RepoController{}.NewRepoFromURI)

	// Start server
	log.Printf(logInfo+"Listening on port: %v", port)
	http.ListenAndServe(":"+port, nil)
}
