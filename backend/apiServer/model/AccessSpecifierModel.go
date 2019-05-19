package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
	"fmt"
)

// AccessSpecifierModel represents code for a single access specifier
type AccessSpecifierModel struct {
	Name      string          `json:"name"`
	Classes   []ClassModel    `json:"classes,omitempty"`
	Functions []FunctionModel `json:"functions,omitempty"`
	Variables []VariableModel `json:"variables,omitempty"`
}

func GetAccessSpecifierObject() *graphql.Object {
	fmt.Println("GetAccessSpecifierObject")
	util.TypeLogger.Debug("%s: Call for GetAccessSpecifierObject", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for GetAccessSpecifierObject", packageName)

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "access_specifier",
		Description: "Access rights.",
		Fields: graphql.Fields{
			"name": &graphql.Field{
				Type:         graphql.String,
				Description: "Identifies access level or type.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if access, ok := p.Source.(AccessSpecifierModel); ok {
						return access.Name, nil
					}
					return nil, nil
				},
			},/*
			"classes": &graphql.Field{
				Type:         graphql.NewList(GetClassObject()),
				Description: "Classes within this access specifier.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if access, ok := p.Source.(AccessSpecifierModel); ok {
						return access.Classes, nil
					}
					return nil, nil
				},
			},*/
			"functions": &graphql.Field{
				Type:         graphql.NewList(GetFunctionObject()),
				Description: "Functions within this access specifier.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if access, ok := p.Source.(AccessSpecifierModel); ok {
						return access.Functions, nil
					}
					return nil, nil
				},
			},/*
			"variables": &graphql.Field{
				Type:         graphql.NewList(GetVariableObject()),
				Description: "Variables within this access specifier.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if access, ok := p.Source.(AccessSpecifierModel); ok {
						return access.Variables, nil
					}
					return nil, nil
				},
			},*/
		},
	})
}