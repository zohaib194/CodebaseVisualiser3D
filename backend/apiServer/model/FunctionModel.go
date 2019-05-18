package model

import(
	"github.com/graphql-go/graphql"
)

// FunctionModel represents code for a single function
type FunctionModel struct {
	Name         string            `json:"name"`
	DeclID       string            `json:"declrator_id"`
	ReturnType   string            `json:"return_type,omitempty"`
	FunctionBody FunctionBodyModel `json:"function_body,omitempty"`
	Parameters   []ParameterModel  `json:"parameters,omitempty"`
	Scope        string            `json:"scope,omitempty"`
	StartLine    int               `json:"start_line"`
	EndLine      int               `json:"end_line"`
}

func GetFunctionObject() *graphql.Object {

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "function",
		Description: "A function.",
		Fields: graphql.Fields{
			"name": &graphql.Field{
				Type: graphql.String,
				Description: "Name of the function.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionModel); ok {
						return function.Name, nil
					}
					return nil, nil
				},
			},
			"declrator_id": &graphql.Field{
				Type: graphql.String,
				Description: "Identifier of the function.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionModel); ok {
						return function.DeclID, nil
					}
					return nil, nil
				},
			},
			"return_type": &graphql.Field{
				Type: graphql.String,
				Description: "Return type of the function.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionModel); ok {
						return function.ReturnType, nil
					}
					return nil, nil
				},
			},
			"function_body": &graphql.Field{
				Type: GetFunctionBodyObject(),
				Description: "Body of the function.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionModel); ok {
						return function.FunctionBody, nil
					}
					return nil, nil
				},
			},
			"parameters": &graphql.Field{
				Type: graphql.NewList(GetParameterObject()),
				Description: "Function Parameters.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionModel); ok {
						return function.Parameters, nil
					}
					return nil, nil
				},
			},
			"scope": &graphql.Field{
				Type: graphql.String,
				Description: "Scope of the function.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionModel); ok {
						return function.Scope, nil
					}
					return nil, nil
				},
			},
			"start_line": &graphql.Field{
				Type: graphql.Int,
				Description: "Where the function starts.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionModel); ok {
						return function.StartLine, nil
					}
					return nil, nil
				},
			},
			"end_line": &graphql.Field{
				Type: graphql.String,
				Description: "Where the function ends.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if function, ok := p.Source.(FunctionModel); ok {
						return function.EndLine, nil
					}
					return nil, nil
				},
			},
		},

	})
}