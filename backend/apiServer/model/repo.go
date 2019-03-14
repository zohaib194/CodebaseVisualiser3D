//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"bytes"
	"encoding/json"
	"os/exec"
	"path"
	"strconv"
	"strings"

	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
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

// ParseResponse is used by ParseDataFromFiles to update channel used by go routine to indicate
// status of request and result
type ParseResponse struct {
	StatusText       string
	Err              error
	CurrentFile      string
	ParsedFileCount  int
	SkippedFileCount int
	FileCount        int
	Result           ProjectModel
}

// Save is expected to run as a go rutine writing to a c.
func (repo RepoModel) Save(c chan SaveResponse) {
	util.TypeLogger.Debug("%s: Call to Save", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to Save", packageName)

	err := DB.add(&repo)

	if err != nil {
		util.TypeLogger.Error("%s: Failed to add to database: %s", packageName, err.Error())
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
	util.TypeLogger.Debug("%s: Call to Load", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to Load", packageName)

	data.File.Parsed = false
	data.File.FileName = file

	// Ready  word count command and execute it.
	cmd := exec.Command("wc", "-l", file)
	output, err := cmd.CombinedOutput()

	if err != nil {
		util.TypeLogger.Error("%s: Failed to count lines in file: %s", err.Error())
		return data, err
	}

	// Split the output on space and grab the first entry (lines in file)
	splitWCOutput := strings.Split(string(output), " ")
	linesOfCode, err := strconv.Atoi(splitWCOutput[0])

	if err != nil {
		util.TypeLogger.Error("%s: Failed to convert string to int: %s", err.Error())
		return data, err
	}

	// Setup the command to parse the file.
	cmd = exec.Command("java", "me.codvis.ast.Main", "-f", file, "-t", target, "-c", "Initial")
	cmd.Dir = JavaParserPath
	output, err = cmd.CombinedOutput()

	if err != nil {
		util.TypeLogger.Error("%s: Failed to execute java parser: %s", err.Error())
		return data, err
	}

	ioReader := bytes.NewReader(output)
	decoder := json.NewDecoder(ioReader)

	if err := decoder.Decode(&data); err != nil {
		util.TypeLogger.Error("%s: Failed to decode json: %s", err.Error())
		return data, err
	}
	data.File.LinesInFile = linesOfCode
	data.File.Parsed = true

	return data, nil
}

// GetRepoByID finds repo in database and returns.
func (repo RepoModel) GetRepoByID(id string) (rep RepoModel, err error) {
	util.TypeLogger.Debug("%s: Call to GetRepoID", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to GetRepoID", packageName)

	exstRepo, err := DB.FindRepoByID(id)

	if err != nil {
		util.TypeLogger.Warn("%s: Failed to find repo in database", packageName)
		return RepoModel{}, err
	}

	return exstRepo, nil
}

// GetRepoFiles finds and returns all files stored in repository directory.
// Excludes directories them selfs (as files) and anything from ".git" folder
func (repo RepoModel) GetRepoFiles() (files string, err error) {
	util.TypeLogger.Debug("%s: Call to  GetRepoFiles", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to  GetRepoFiles", packageName)

	cmd := exec.Command("find", RepoPath+"/"+repo.ID.Hex(), "-type", "f", "-not", "-path", "*/.git/*")
	cmd.Dir = JavaParserPath
	bytes, err := cmd.CombinedOutput()

	if err != nil {
		util.TypeLogger.Error("%s: Error executing finde %s", packageName, err.Error())
		return "", err
	}

	return string(bytes), nil
}

// SanitizeFilePaths removes the repopath from the filepaths.
func (repo RepoModel) SanitizeFilePaths(projectModel ProjectModel) {
	util.TypeLogger.Debug("%s: Call to SanitizeFilePaths", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to SanitizeFilePaths", packageName)

	for index, file := range projectModel.Files {
		projectModel.Files[index].File.FileName = strings.Replace(file.File.FileName, RepoPath+"/", "", -1)
	}
}

// ParseDataFromFiles fetch all functions from gives files set.
func (repo RepoModel) ParseDataFromFiles(files string, responsePerNFiles int, c chan ParseResponse) {
	util.TypeLogger.Debug("%s: Call to  ParseDataFromFiles", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to  ParseDataFromFiles", packageName)

	response := ParseResponse{StatusText: "Parsing"}
	var projectModel ProjectModel

	filesList := strings.Split(strings.TrimSuffix(files, "\n"), "\n")

	response.FileCount = len(filesList)

	for n, sourceFile := range filesList {
		// Search for cpp files
		var err error
		var data FilesModel

		response.CurrentFile = path.Base(sourceFile)

		switch fileExtention := path.Ext(sourceFile); fileExtention {
		case ".cpp":
			data, err = repo.Load(sourceFile, "cpp") // Fetch function names from the file.
			response.ParsedFileCount++

		case ".hpp":
			data, err = repo.Load(sourceFile, "cpp")
			response.ParsedFileCount++

		case ".java":
			data, err = repo.Load(sourceFile, "java")
			response.ParsedFileCount++

		default:
			data = FilesModel{File: FileModel{Parsed: false, FileName: sourceFile}}
			response.SkippedFileCount++
		}

		if err != nil {
			util.TypeLogger.Error("%s: Failed to parse file: %s", packageName, err.Error())
			data = FilesModel{File: FileModel{Parsed: false, FileName: sourceFile}}

		}

		projectModel.Files = append(projectModel.Files, data)
		if n%responsePerNFiles == 0 {
			c <- response
		}

	}

	repo.SanitizeFilePaths(projectModel)

	response.StatusText = "Done"
	response.Result = projectModel

	c <- response

	return
}

// FetchAll fetches all the repositories.
func (repo RepoModel) FetchAll() (repoModels []bson.M, err error) {
	util.TypeLogger.Debug("%s: Call to FetchAll", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to FetchAll", packageName)

	reposModels, err := DB.FindAllURI()

	if err != nil {
		util.TypeLogger.Debug("%s: Failed to find repository", packageName)
		return []bson.M{}, err
	}

	return reposModels, nil
}
