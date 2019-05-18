//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"github.com/graphql-go/graphql"
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

func GetCodeSnippetObject() *graphql.Object {
	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "code_snippet",
		Description: "A section of code.",
		Fields: graphql.Fields{
			"file_path": &graphql.Field{
				Type:         graphql.String,
				Description: "File where snippet is from.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if snippet, ok := p.Source.(CodeSnippetModel); ok {
						return snippet.FilePath, nil
					}
					return nil, nil
				},
			},
			"id": &graphql.Field{
				Type:         graphql.Int,
				Description: "ID of the associated Git repository.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if snippet, ok := p.Source.(CodeSnippetModel); ok {
						return snippet.ID, nil
					}
					return nil, nil
				},
			},
			"start_line": &graphql.Field{
				Type:         graphql.Int,
				Description: "Where in the line the snippet starts",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if snippet, ok := p.Source.(CodeSnippetModel); ok {
						return snippet.StartLine, nil
					}
					return nil, nil
				},
			},
			"end_line": &graphql.Field{
				Type:         graphql.Int,
				Description: "Where in the line the snippet ends.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if snippet, ok := p.Source.(CodeSnippetModel); ok {
						return snippet.EndLine, nil
					}
					return nil, nil
				},
			},
		},
	})
}