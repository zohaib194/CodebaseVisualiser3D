//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"bytes"
	"encoding/json"
	"log"
	"os/exec"
	"strconv"
	"strings"
	"path"

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
	data.File.Parsed = false
	data.File.FileName = file

	// Ready  word count command and execute it.
	cmd := exec.Command("wc", "-l", file)
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Println("Error could not count lines!: ", err.Error())
		return data, err
	}

	// Split the output on space and grab the first entry (lines in file)
	splitWCOutput := strings.Split(string(output), " ")
	linesOfCode, err := strconv.Atoi(splitWCOutput[0])

	if err != nil {
		log.Println("Error could not convert string to int: ", err.Error())
		return data, err
	}

	// Setup the command to parse the file.
	cmd = exec.Command("java", "me.codvis.ast.Main", "-f", file, "-t", target, "-c", "Initial")
	cmd.Dir = JavaParserPath
	output, err = cmd.CombinedOutput()

	if err != nil {
		log.Println("Error executing java parser: ", err.Error())
		return data, err
	}

	ioReader := bytes.NewReader(output)
	decoder := json.NewDecoder(ioReader)

	if err := decoder.Decode(&data); err != nil {
		log.Fatal("Could not decode json error: ", err.Error())
		return data, err
	}
	data.File.LinesInFile = linesOfCode
	data.File.Parsed = true

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

// GetRepoFiles finds and returns all files stored in repository directory.
// Excludes directories them selfs (as files) and anything from ".git" folder
func (repo RepoModel) GetRepoFiles() (files string, err error) {
	cmd := exec.Command("find", RepoPath+"/"+repo.ID.Hex(), "-type", "f", "-not", "-path", "*/.git/*")
	cmd.Dir = JavaParserPath
	bytes, err := cmd.CombinedOutput()

	if err != nil {
		log.Println("Error executing find: ", err.Error())
		return "", err
	}

	return string(bytes), nil
}

// SanitizeFilePath removes the repopath from the filepaths.
func (repo RepoModel) SanitizeFilePaths(projectModel ProjectModel) {
	for index, file := range projectModel.Files {
		projectModel.Files[index].File.FileName = strings.Replace(file.File.FileName, RepoPath+"/", "", -1)
	}
}

// ParseDataFromFiles fetch all functions from gives files set.
func (repo RepoModel) ParseDataFromFiles(files string) (projectModel ProjectModel, err error) {
	for _, sourceFile := range strings.Split(strings.TrimSuffix(files, "\n"), "\n") {
		// Search for cpp files
		var err error
		var data FilesModel

		switch fileExtention := path.Ext(sourceFile); fileExtention {
			case ".cpp":
				data, err = repo.Load(sourceFile, "cpp")		// Fetch function names from the file.

			case ".java":
				data, err = repo.Load(sourceFile, "java")

			default:	
				data = FilesModel{File: FileModel{Parsed: false, FileName: sourceFile}}
		}

		if err != nil {
			log.Println("Could not parse error: ", err.Error())
			data = FilesModel{File: FileModel{Parsed: false, FileName: sourceFile}}
		}

		projectModel.Files = append(projectModel.Files, data)


	}
	
	repo.SanitizeFilePaths(projectModel)
	
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
