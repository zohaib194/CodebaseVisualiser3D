package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
)

// FileWrapperModel is a wrapper for FileModel for json parsing
type FileWrapperModel struct {
	File FileModel `json:"file"`
}

// FileModel represents a single code file
type FileModel struct {
	Parsed          bool                  `json:"parsed"`
	FileName        string                `json:"file_name"`
	Functions       []FunctionModel       `json:"functions,omitempty"`
	Namespaces      []NamespaceModel      `json:"namespaces,omitempty"`
	UsingNamespaces []UsingNamespaceModel `json:"using_namespaces,omitempty"`
	Includes        []string              `json:"includes,omitempty"`
	Classes         []ClassModel          `json:"classes,omitempty"`
	Variables       []VariableModel       `json:"variables,omitempty"`
	LinesInFile     int                   `json:"linesInFile"`
}

var fileObject = GetFileObject()

func GetFileObject() *graphql.Object {
	util.TypeLogger.Debug("%s: Call for GetFileObject", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for GetFileObject", packageName)

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "file",
		Description: "A code file.",
		Fields: graphql.Fields{
			"parsed": &graphql.Field{
				Type: graphql.Boolean,
				Description: "A whether or not the file has been parsed",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.Parsed, nil
					}
					return nil, nil
				},
			},
			"file_name": &graphql.Field{
				Type: graphql.String,
				Description: "Name of the file.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.FileName, nil
					}
					return nil, nil
				},
			},
			"functions": &graphql.Field{
				Type: graphql.NewList(functionObject),
				Description: "Functions within this file.",
			},
			"namespaces": &graphql.Field{
				Type: graphql.NewList(namespaceObject),
				Description: "Namespaces being declared.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.Namespaces, nil
					}
					return nil, nil
				},
			},
			"using_namespaces": &graphql.Field{
				Type: graphql.NewList(usingNamespaceObject),
				Description: "Namespaces within this file.",
			},
			"includes": &graphql.Field{
				Type: graphql.NewList(graphql.String),
				Description: "Files included by this file.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.Includes, nil
					}
					return nil, nil
				},
			},
			"classes": &graphql.Field{
				Type: graphql.NewList(classObject),
				Description: "Classes within this file.",
			},
			"variables": &graphql.Field{
				Type: graphql.NewList(variableObject),
				Description: "Variables being defined.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.Variables, nil
					}
					return nil, nil
				},
			},
			"lines_count": &graphql.Field{
				Type: graphql.Int,
				Description: "Number of lines in the file.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.LinesInFile, nil
					}
					return nil, nil
				},
			},
		},
	})
}