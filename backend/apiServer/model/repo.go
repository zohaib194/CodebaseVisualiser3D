//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"bytes"
	"encoding/json"
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
	URI string        `json:"uri"`                     // Where the repository was found
	ID  bson.ObjectId `json:"id" bson:"_id,omitempty"` // Folder name where repo is stored
}

// SaveResponse is used by save function to update channel used by go rutine to indicate
// status of the save request.
type SaveResponse struct {
	ID         string
	StatusText string
	Err        error
}

// Save is expected to run as a go rutine writing to a c.
func (repo RepoModel) Save(c chan SaveResponse) {

	err := DB.add(&repo)

	if err != nil {
		log.Println("Could not add to database: ", err)
		// Send the existing repo id with status text failed.
		c <- SaveResponse{ID: repo.ID.Hex(), StatusText: "Failed", Err: err}
		return
	}

	c <- SaveResponse{ID: repo.ID.Hex(), StatusText: "Cloning", Err: nil}

	// Clone repository into storage location with name given by database
	cmd := exec.Command("git", "-C", RepoPath, "clone", repo.URI, repo.ID.Hex())
	_, err = cmd.Output() // TODO: Validate that git clone went well and prevent request for rsa password

	c <- SaveResponse{ID: repo.ID.Hex(), StatusText: "Done", Err: err}

	return
}

// Load loads java application to parse a specified file.
func (repo RepoModel) Load(file string, target string) (data FilesModel, err error) {
	// Setup the command to parse the file.
	cmd := exec.Command("java", "me.codvis.ast.Main", "-f", file, "-t", target, "-c", "Initial")
	cmd.Dir = JavaParserPath
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Println("Error executing java parser: ", err.Error())
		return FilesModel{}, err
	}

	ioReader := bytes.NewReader(output)
	decoder := json.NewDecoder(ioReader)

	if err := decoder.Decode(&data); err != nil {
		log.Fatal("Could not decode json error: ", err.Error())
		return FilesModel{}, err
	}

	return data, nil
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
	cmd := exec.Command("find", RepoPath+"/"+repo.ID.Hex())
	cmd.Dir = JavaParserPath
	bytes, err := cmd.CombinedOutput()

	if err != nil {
		log.Println("Error executing find: ", err.Error())
		return "", err
	}

	return string(bytes), nil
}

// SanitizeFilePath removes the repopath from the filepaths.
func (repo RepoModel) SanitizeFilePath(projectModel ProjectModel) {
	for index, file := range projectModel.Files {
		projectModel.Files[index].File.FileName = strings.Replace(file.File.FileName, RepoPath+"/", "", -1)
	}
}

// ParseFunctionsFromFiles fetch all functions from gives files set.
func (repo RepoModel) ParseFunctionsFromFiles(files string) (projectModel ProjectModel, err error) {
	for _, sourceFiles := range strings.Split(strings.TrimSuffix(files, "\n"), "\n") {
		// Search for cpp files
		if strings.Contains(sourceFiles, ".cpp") || strings.Contains(sourceFiles, ".hpp"){
			// Fetch function names from the file.
			data, err := repo.Load(sourceFiles, "cpp")

			if err != nil {
				log.Println("Could not parse error: ", err.Error())
				return ProjectModel{}, err
			}

			projectModel.Files = append(projectModel.Files, data)

		} else if strings.Contains(sourceFiles, ".java") { // Search for java files

			data, err := repo.Load(sourceFiles, "java")

			if err != nil {
				log.Println("Could not parse error: ", err.Error())
				return ProjectModel{}, err
			}

			projectModel.Files = append(projectModel.Files, data)
		}

		repo.SanitizeFilePath(projectModel)

	}
	return projectModel, nil
}

// FetchAll fetches all the repositories.
func (repo RepoModel) FetchAll() (repoModels []bson.M, err error) {
	reposModels, err := DB.FindAllURI()

	if err != nil {
		log.Println("Could not find repositories error: ", err.Error())
		return []bson.M{}, err
	}

	return reposModels, nil
}
