//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"errors"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
	"gopkg.in/mgo.v2/bson"
	"os/exec"
	"strconv"
	"strings"
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
	util.TypeLogger.Info("%s: Received request for implementation", packageName)
	defer util.TypeLogger.Info("%s: Ended request for implementation", packageName)

	// Ready  word count command and execute it.
	cmd := exec.Command("wc", "-l", RepoPath+"/"+codeSnippet.FilePath)
	output, err := cmd.CombinedOutput()

	if err != nil {
		util.TypeLogger.Error("%s: Failed to count lines in file: %s", packageName, err.Error())
		return "", errors.New("Failed to count lines in file")
	}

	// Split the output on space and grab the first entry (lines in file)
	linesTotal, err := strconv.Atoi(strings.Split(string(output), " ")[0])

	if codeSnippet.StartLine > linesTotal {
		util.TypeLogger.Warn("%s: StartLine out of range", packageName)
		return "", errors.New("StartLine out of range")
	}
	if err != nil {
		util.TypeLogger.Error("%s: Failed to convert string to int: %s", packageName, err.Error())
		return "", errors.New("Failed to count lines in file")
	}

	// Command to run sed for fetching file content.
	cmd = exec.Command("sed", "-n", strconv.Itoa(codeSnippet.StartLine)+","+strconv.Itoa(codeSnippet.EndLine)+"p;", RepoPath+"/"+codeSnippet.FilePath)
	output, err = cmd.CombinedOutput()

	if err != nil {
		util.TypeLogger.Error("%s: Error executing sed parser: %s", packageName, err.Error())
		return "", err
	}

	return string(output), err
}
