//main deals with environment and api definition.
package main

import (
	"github.com/zohaib194/CodebaseVisualizer3D/apiServer/controller"
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
	repoPath := os.Getenv("REPOSITORY_PATH")

	// Validate variables
	if len(port) == 0 {
		log.Fatal(logError + "$PORT was not set")
	}
	if len(repoPath) == 0 {
		log.Fatal(logError + "$REPOSITORY_PATH was not set")
	}

	// API routings
	http.HandleFunc("/repo/add", controller.Repo{}.NewRepoFromURI)

	// Start server
	log.Printf(logInfo+"Listening on port: %v", port)
	http.ListenAndServe(":"+port, nil)
}
