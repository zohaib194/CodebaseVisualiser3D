//main deals with environment and api definition.
package main

import (
	"net/http"
	"os"

	"github.com/gorilla/mux"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/controller"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/model"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
)

var packageName = "main"

// Indicate the importance of a warning
func main() {
	router := mux.NewRouter()

	// Get environment variables
	port := os.Getenv("PORT")
	model.RepoPath = os.Getenv("REPOSITORY_PATH")
	dbLocation := os.Getenv("DB_LOCATION")
	model.JavaParserPath = os.Getenv("JAVA_PARSER")
	logLevel := os.Getenv("LOG_LEVEL")
	logFile := os.Getenv("LOG_FILE")

	// Validate variables
	if len(port) == 0 {
		util.TypeLogger.Fatal("$PORT was not set")
	}
	if len(model.RepoPath) == 0 {
		util.TypeLogger.Fatal("$REPOSITORY_PATH was not set")
	}
	if len(dbLocation) == 0 {
		util.TypeLogger.Fatal("$DB_LOCATION was not set")
	}
	if len(model.JavaParserPath) == 0 {
		util.TypeLogger.Fatal("$JAVA_PARSER was not set")
	}
	if len(logLevel) == 0 || !util.SetLogLevel(logLevel) {
		util.TypeLogger.Warn("$LOG_LEVEL not set")
	}
	if len(logFile) == 0 || !util.SetLogFile(logFile) {
		util.TypeLogger.Warn("$LOG_FILE not set, fallback to stdout")
	}

	// Database setup
	util.TypeLogger.Info("%s: Setting up database", packageName)
	model.DB.DatabaseURL = dbLocation
	if err := model.DB.Init(); err != nil {
		util.TypeLogger.Fatal("Could not initialize database")
	}

	// API routings
	util.TypeLogger.Info("%s: Setting up api routes", packageName)
	router.HandleFunc("/repo/add", controller.RepoController{}.NewRepoFromURI)
	router.HandleFunc("/repo/list", controller.RepoController{}.GetAllRepos)
	router.HandleFunc("/repo/{repoId}/initial/", controller.RepoController{}.ParseInitial)
	router.HandleFunc("/repo/{repoId}/file/read/", controller.CodeSnippetController{}.GetImplementation)

	// Start server
	util.TypeLogger.Info("%s: Listening on port: %s", packageName, port)
	http.ListenAndServe(":"+port, router)
}
