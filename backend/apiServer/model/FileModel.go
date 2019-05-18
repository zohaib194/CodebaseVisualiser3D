package model

import(
	"github.com/graphql-go/graphql"
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

func GetFileObject() *graphql.Object {

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
				Type: graphql.NewList(GetFunctionObject()),
				Description: "Functions being declared or defined.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.Functions, nil
					}
					return nil, nil
				},
			},
			"namespaces": &graphql.Field{
				Type: graphql.NewList(GetNamespaceObject()),
				Description: "Namespaces being declared.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.Namespaces, nil
					}
					return nil, nil
				},
			},
			"using_namespaces": &graphql.Field{
				Type: graphql.NewList(GetUsingNamespaceObject()),
				Description: "Extraction of a namespace.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.UsingNamespaces, nil
					}
					return nil, nil
				},
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
				Type: graphql.NewList(GetClassObject()),
				Description: "Classes being defined.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if file, ok := p.Source.(FileModel); ok {
						return file.Classes, nil
					}
					return nil, nil
				},
			},
			"variables": &graphql.Field{
				Type: graphql.NewList(GetVariableObject()),
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