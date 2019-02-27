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

	"gopkg.in/mgo.v2/bson"
)

// RepoPath is the path where git repositories are stored.
var RepoPath string

// JavaParserPath is the path where is java parser is stored.
var JavaParserPath string

// RepoModel represents metadata for a git repository.
type RepoModel struct {
	URI string        `json:"uri"`                    // Where the repository was found
	ID  bson.ObjectId `json:"-" bson:"_id,omitempty"` // Folder name where repo is stored
}

// Save saves repo to database and clones repository
func (repo RepoModel) Save() (string, error) {

	err := DB.add(&repo)

	if err != nil {
		log.Println("Could not add to database: ", err)
		return "", err
	}

	log.Println(repo.ID)

	// Clone repository into storage location with name given by database
	cmd := exec.Command("git", "-C", RepoPath, "clone", repo.URI, repo.ID.Hex())
	_, err = cmd.Output() // TODO: Validate that git clone went well and prevent request for rsa password

	return repo.ID.Hex(), err
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

// ParseDataFromFiles fetch all functions from gives files set.
func (repo RepoModel) ParseDataFromFiles(files string) (projectModel ProjectModel, err error) {
	for _, sourceFiles := range strings.Split(strings.TrimSuffix(files, "\n"), "\n") {
		// Search for cpp files
		if strings.Contains(sourceFiles, ".cpp") {
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
