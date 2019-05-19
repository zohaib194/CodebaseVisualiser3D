package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
	"fmt"
)

// FunctionBodyModel represents calls and variable from a function.
type FunctionBodyModel struct {
	Calls     []CallModel     `json:"calls,omitempty"`
	Variables []VariableModel `json:"variables,omitempty"`
}

var functionBodyObject = getFunctionBodyObject()

func getFunctionBodyObject() *graphql.Object {
	fmt.Println("GetFunctionBodyObject")
	util.TypeLogger.Debug("%s: Call for GetFunctionBodyObject", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for GetFunctionBodyObject", packageName)

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "function_body",
		Description: "Body with functionality of a function.",
		Fields: graphql.Fields{
			"calls": &graphql.Field{
				Type:         graphql.NewList(GetCallObject()),
				Description: "Calls made by this function.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionBodyModel); ok {
						return function.Calls, nil
					}
					return nil, nil
				},
			},
			"variables": &graphql.Field{
				Type: graphql.NewList(variableObject),
				Description: "Variables within class.",
			},
		},
	})
}