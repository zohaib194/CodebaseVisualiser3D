package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
	"fmt"
)

// VariableModel represents a variable from code.
type VariableModel struct {
	Name string `json:"name"`
	Type string `json:"type"`
}

func GetVariableObject() *graphql.Object {
	fmt.Println("GetVariableObject")
	util.TypeLogger.Debug("%s: Call for GetVariableObject", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for GetVariableObject", packageName)

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "variable",
		Description: "Variable that has been defined.",
		Fields: graphql.Fields{
			"name": &graphql.Field{
				Type:         graphql.String,
				Description: "Name of the variable.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if variable, ok := p.Source.(VariableModel); ok {
						return variable.Name, nil
					}
					return nil, nil
				},
			},
			"type": &graphql.Field{
				Type:         graphql.String,
				Description: "Type of the variable.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if variable, ok := p.Source.(VariableModel); ok {
						return variable.Type, nil
					}
					return nil, nil
				},
			},
		},
	})
}