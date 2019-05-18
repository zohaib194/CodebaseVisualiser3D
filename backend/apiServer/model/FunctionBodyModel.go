package model

import(
	"github.com/graphql-go/graphql"
)

// FunctionBodyModel represents calls and variable from a function.
type FunctionBodyModel struct {
	Calls     []CallModel     `json:"calls,omitempty"`
	Variables []VariableModel `json:"variables,omitempty"`
}

func GetFunctionBodyObject() *graphql.Object {

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
				Type:         graphql.NewList(GetVariableObject()),
				Description: "Variables defined in this function.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionBodyModel); ok {
						return function.Variables, nil
					}
					return nil, nil
				},
			},
		},
	})
}