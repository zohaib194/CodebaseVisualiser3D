package model

import(
	"github.com/graphql-go/graphql"
)

// ScopeModel represents the scope and name of the function call.
type ScopeModel struct {
	Identifier string `json:"identifier"`
	Type       string `json:"type,omitempty"`
}

func GetScopeObject() *graphql.Object {

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "scope",
		Description: "Scope identifier.",
		Fields: graphql.Fields{
			"Identifier": &graphql.Field{
				Type:         graphql.String,
				Description: "Identifies the scope.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if scope, ok := p.Source.(ScopeModel); ok {
						return scope.Identifier, nil
					}
					return nil, nil
				},
			},
			"type": &graphql.Field{
				Type:         graphql.String,
				Description: "Type of the scope.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if scope, ok := p.Source.(ScopeModel); ok {
						return scope.Type, nil
					}
					return nil, nil
				},
			},
		},
	})
}