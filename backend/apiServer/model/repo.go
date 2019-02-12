//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"log"
	"os/exec"
	"strings"

	"gopkg.in/mgo.v2/bson"
)

// RepoPath is the path where git repositories are stored.
var RepoPath string

// JavaParserPath is the path where is java parser is stored.
var JavaParserPath string

// RepoModel represents metadata for a git repository.
type RepoModel struct {
	URI      string        `json:"uri"`                    // Where the repository was found
	ID       bson.ObjectId `json:"-" bson:"_id,omitempty"` // Folder name where repo is stored
}

// Save saves repo to database and clones repository
func (repo RepoModel) Save() error {

	err := DB.add(&repo)

	if err != nil {
		log.Println("Could not add to database: ", err)
		return err
	}

	log.Println(repo.ID)

	// Clone repository into storage location with name given by database
	cmd := exec.Command("git", "-C", RepoPath, "clone", repo.URI, repo.ID.Hex())
	_, err = cmd.Output() // TODO: Validate that git clone went well and prevent request for rsa password

	return err
}

// Load loads java application to parse a specified file.
func (repo RepoModel) Load(file string, target string) (function Function, err error) {

	// Setup the command to parse the file.
	cmd := exec.Command("java", "me.codvis.ast.Main", "-f", file, "-t", target)
	cmd.Dir = JavaParserPath
	bytes, err := cmd.CombinedOutput()

	if err != nil {
		log.Println("Error executing java parser: ", err.Error())
		return Function{}, err
	}

	// Removing unnecessary data from the output.
	output := strings.Replace(string(bytes), "function_name", "", -1)
	output = strings.Replace(string(output), "\n", "", -1)
	for _, line := range strings.Split(strings.TrimSuffix(output, "\n"), ": ") {
		if line != "" {

			// Adding function names into object.
			function.Name = append(function.Name, map[string]string{"name": line})

		}
	}

	function.File = file

	return function, nil
}

// GetRepoByID finds repo in database and returns.
func (repo RepoModel) GetRepoByID(id string) (rep RepoModel, err error) {
	exstRepo, err := DB.FindRepoByID(id)

	if err != nil {
		log.Println("Could not find repo in database: ", err.Error())
		return RepoModel{}, err
	}

	return exstRepo, nil
}

// GetRepoFile finds and return all files stored in repository directory.
func (repo RepoModel) GetRepoFile() (files string, err error) {
	cmd := exec.Command("find", RepoPath + "/" + ID.Hex())
	cmd.Dir = path
	bytes, err := cmd.CombinedOutput()

	if err != nil {
		log.Println("Error executing find: ", err.Error())
		return "", err
	}

	return string(bytes), nil
}

// ParseFunctionsFromFiles fetch all functions from gives files set.
func (repo RepoModel) ParseFunctionsFromFiles(files string) (functions Functions, err error) {

	for _, sourceFiles := range strings.Split(strings.TrimSuffix(files, "\n"), "\n") {

		// Search for cpp files
		if strings.Contains(sourceFiles, ".cpp") {

			// Fetch function names from the file.
			function, err := Load(sourceFiles, "cpp")

			if err != nil {
				log.Println("Could not parse error: ", err.Error())
				return Functions{}, err
			}

			functions.Functions = append(functions.Functions, function)

		} else if strings.Contains(sourceFiles, ".java") { // Search for java files

			function, err := Load(sourceFiles, "java")

			if err != nil {
				log.Println("Could not parse error: ", err.Error())
				return Functions{}, err
			}

			functions.Functions = append(functions.Functions, function)
		}
	}

	return functions, nil
}