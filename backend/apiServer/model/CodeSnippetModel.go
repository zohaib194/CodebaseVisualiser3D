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

// CodeSnippetModel represents metadata for a file from a git project.
type CodeSnippetModel struct {
	FilePath  string        `json:"file_path"`              // File name to be searched through.
	ID        bson.ObjectId `json:"-" bson:"_id,omitempty"` // Folder name where repo is stored
	StartLine int           `json:"start_line"`
	EndLine   int           `json:"end_line"`
}

// FetchLinesOfCode fetch loc from specified range.
func (codeSnippet CodeSnippetModel) FetchLinesOfCode() (string, error) {

	// Command to run sed for fetching file content.
	cmd := exec.Command("sed", "-n", strconv.Itoa(codeSnippet.StartLine)+","+strconv.Itoa(codeSnippet.EndLine)+"p;", RepoPath+"/"+codeSnippet.FilePath)
	output, err := cmd.CombinedOutput()

	if err != nil {
		log.Println("Error executing sed parser: ", err.Error())
		return "", err
	}

	return string(output), err
}
