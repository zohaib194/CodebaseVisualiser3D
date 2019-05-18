//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"encoding/json"
	"os/exec"
	"path"
	"strconv"
	"strings"

	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
	"gopkg.in/mgo.v2/bson"
)

// RepoPath is the path where git repositories are stored.
var RepoPath string

// JavaParserPath is the path where is java parser is stored.
var JavaParserPath string

// RepoModel represents metadata for a git repository.
type RepoModel struct {
	URI        string        `json:"uri"`                     // Where the repository was found
	ID         bson.ObjectId `json:"id" bson:"_id,omitempty"` // Folder name where repo is stored
	ParsedRepo ProjectModel  `json:"parsedrepo,omitempty"`    // Parsed repository in json format
}

// SaveResponse is used by save function to update channel used by go routine to indicate
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

	err := DB.Add(&repo)

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
func (repo RepoModel) Load(file string, target string) (data FileModel, err error) {
	util.TypeLogger.Debug("%s: Call to Load", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to Load", packageName)

	data.Parsed = false
	data.FileName = file

	// Ready  word count command and execute it.
	cmd := exec.Command("wc", "-l", file)
	output, err := cmd.CombinedOutput()

	if err != nil {
		util.TypeLogger.Error("%s: Failed to count lines in file: %s", packageName, err.Error())
		return data, err
	}

	// Split the output on space and grab the first entry (lines in file)
	splitWCOutput := strings.Split(string(output), " ")
	linesOfCode, err := strconv.Atoi(splitWCOutput[0])

	if err != nil {
		util.TypeLogger.Error("%s: Failed to convert string to int: %s", packageName, err.Error())
		return data, err
	}

	// Setup the command to parse the file.
	cmd = exec.Command("java", "me.codvis.ast.Main", "-f", file, "-t", target, "-c", "Initial")
	cmd.Dir = JavaParserPath
	stdout, err := cmd.StdoutPipe()

	if err != nil {
		util.TypeLogger.Error("%s: Failed to attach command to stdout for java parser: %s", packageName, err.Error())
		return data, err
	}

	if err := cmd.Start(); err != nil {
		util.TypeLogger.Error("%s: Failed to execute java parser: %s", packageName, err.Error())
		return data, err
	}

	fileWrapper := FileWrapperModel{File: data}

	if err := json.NewDecoder(stdout).Decode(&fileWrapper); err != nil {
		util.TypeLogger.Error("%s: Failed to decode json: %s", packageName, err.Error())
		return fileWrapper.File, err
	}

	if err := cmd.Wait(); err != nil {
		util.TypeLogger.Error("%s: Failed to exit the command for java parser : %s", packageName, err.Error())
		return fileWrapper.File, err
	}

	fileWrapper.File.LinesInFile = linesOfCode
	fileWrapper.File.Parsed = true

	return fileWrapper.File, nil
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
		util.TypeLogger.Error("%s: Error executing find %s", packageName, err.Error())
		return "", err
	}

	return string(bytes), nil
}

// UpdateRepo updates the repo model with repo in db.
func (repo RepoModel) UpdateRepo() error {
	util.TypeLogger.Debug("%s: Call to UpdateRepoByID", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to UpdateRepoByID", packageName)

	err := DB.Update(&repo)

	if err != nil {
		util.TypeLogger.Error("%s: Failed to update database: %s", packageName, err.Error())
		return err
	}

	return nil
}

// SanitizeFilePaths removes the repopath from the filepaths.
func (repo RepoModel) SanitizeFilePaths(projectModel ProjectModel) {
	util.TypeLogger.Debug("%s: Call to SanitizeFilePaths", packageName)
	defer util.TypeLogger.Debug("%s: Ended call to SanitizeFilePaths", packageName)

	for index, file := range projectModel.Files {
		projectModel.Files[index].FileName = strings.Replace(file.FileName, RepoPath+"/", "", -1)
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
		var data FileModel

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
			data = FileModel{Parsed: false, FileName: sourceFile}
			response.SkippedFileCount++
		}

		if err != nil {
			util.TypeLogger.Error("%s: Failed to parse file: %s", packageName, err.Error())
			data = FileModel{Parsed: false, FileName: sourceFile}

		}
		projectModel.Files = append(projectModel.Files, data)
		if n % responsePerNFiles == 0 {
			c <- response
		}

	}

	repo.SanitizeFilePaths(projectModel)

	repo.ParsedRepo = projectModel
	repo.UpdateRepo()

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
		util.TypeLogger.Warn("%s: Failed to find repository", packageName)
		return []bson.M{}, err
	}

	return reposModels, nil
}
func GetRepositoryObject() *graphql.Object {

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "repo",
		Description: "A Git repository.",
		Fields: graphql.Fields{
			"id": &graphql.Field{
				Type:        graphql.NewNonNull(graphql.String),
				Description: "The Bson id of the Git Repository.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if repo, ok := p.Source.(RepoModel); ok {
						return repo.ID, nil
					}
					return nil, nil
				},
			},
			"content": &graphql.Field{
				Type: GetProjectObject(),
				Description: "The parsed content.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if repo, ok := p.Source.(RepoModel); ok {
						return repo.ParsedRepo, nil
					}
					return nil, nil
				},
			},
		},
	})
}