package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
)

// CallModel represents a function call from code.
type CallModel struct {
	Identifier string       `json:"identifier"`
	Scope      []ScopeModel `json:"scopes,omitempty"`
}

func GetCallObject() *graphql.Object {

	util.TypeLogger.Debug("%s: Call for GetCallObject", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for GetCallObject", packageName)

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "call",
		Description: "A function call or assignment.",
		Fields: graphql.Fields{
			"identifier": &graphql.Field{
				Type:         graphql.String,
				Description: "Identifies the function being called.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if call, ok := p.Source.(CallModel); ok {
						return call.Identifier, nil
					}
					return nil, nil
				},
			},
			"scope": &graphql.Field{
				Type:         graphql.NewList(GetScopeObject()),
				Description: "scopes indicating the position of the function being called.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if call, ok := p.Source.(CallModel); ok {
						return call.Scope, nil
					}
					return nil, nil
				},
			},
		},
	})
}