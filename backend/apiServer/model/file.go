//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"log"
	"os/exec"
	"strconv"
	//"bytes"
	//"encoding/json"

	"gopkg.in/mgo.v2/bson"
)

// File represents metadata for a file from a git project.
type File struct {
	FilePath  string        `json:"file_path"`              // File name to be searched through.
	ID        bson.ObjectId `json:"-" bson:"_id,omitempty"` // Folder name where repo is stored
	StartLine int           `json:"start_line"`
	EndLine   int           `json:"end_line"`
}

// FetchLinesOfCode fetch loc from specified range.
func (file File) FetchLinesOfCode() (string, error) {

	// Command to run sed for fetching file content.
	cmd := exec.Command("sed", "-n", strconv.Itoa(file.StartLine)+","+strconv.Itoa(file.EndLine)+"p;", RepoPath+"/"+file.FilePath)
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Println("Error executing sed parser: ", err.Error())
		return "", err
	}

	return string(output), err
}
