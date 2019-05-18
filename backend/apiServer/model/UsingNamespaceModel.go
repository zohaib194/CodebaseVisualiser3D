package model

import(
	"github.com/graphql-go/graphql"
)


// UsingNamespaceModel represents the use of namespace.
type UsingNamespaceModel struct {
	Name   string `json:"name"`
	LineNr int    `json:"lineNr"`
}

func GetUsingNamespaceObject() *graphql.Object {

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "using_namespace",
		Description: "An extraction from a namespace.",
		Fields: graphql.Fields{
			"name": &graphql.Field{
				Type: graphql.String,
				Description: "Name of the extracted namespace.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if usingNamespace, ok := p.Source.(UsingNamespaceModel); ok {
						return usingNamespace.Name, nil
					}
					return nil, nil
				},
			},
			"LineNr": &graphql.Field{
				Type: graphql.String,
				Description: "Line where the namespace is extracted.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if usingNamespace, ok := p.Source.(UsingNamespaceModel); ok {
						return usingNamespace.LineNr, nil
					}
					return nil, nil
				},
			},
		},
	})
}