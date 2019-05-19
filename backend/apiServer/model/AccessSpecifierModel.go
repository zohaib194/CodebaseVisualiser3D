package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
)

// AccessSpecifierModel represents code for a single access specifier
type AccessSpecifierModel struct {
	Name      string          `json:"name"`
	Classes   []ClassModel    `json:"classes,omitempty"`
	Functions []FunctionModel `json:"functions,omitempty"`
	Variables []VariableModel `json:"variables,omitempty"`
}

var accessSpecifierObject = getAccessSpecifierObject()

func getAccessSpecifierObject() *graphql.Object {
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
			},
			"variables": &graphql.Field{
				Type: graphql.NewList(variableObject),
				Description: "Variables within class.",
			},
			"functions": &graphql.Field{
				Type:         graphql.NewList(functionObject),
				Description: "Functions within this access specifier.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if access, ok := p.Source.(AccessSpecifierModel); ok {
						return access.Functions, nil
					}
					return nil, nil
				},
			},
		},
	})


	/* Handled by getClassObject

	accessSpecifierObject.AddFieldConfig("classes", &graphql.Field{
		Type: graphql.NewList(classObject),
		Description: "Classes within class.",
	})*/
}