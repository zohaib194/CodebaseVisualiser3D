package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
)

// ParameterModel represents a parameter from code.
type ParameterModel struct {
	Name string `json:"name"`
	Type string `json:"type"`
}

func GetParameterObject() *graphql.Object {
	util.TypeLogger.Debug("%s: Call for GetParameterObject", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for GetParameterObject", packageName)

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "parameter",
		Description: "An injected parameter.",
		Fields: graphql.Fields{
			"name": &graphql.Field{
				Type:         graphql.String,
				Description: "The project files.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if parameter, ok := p.Source.(ParameterModel); ok {
						return parameter.Name, nil
					}
					return nil, nil
				},
			},
			"type": &graphql.Field{
				Type:         graphql.String,
				Description: "Type of the parameter.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if parameter, ok := p.Source.(ParameterModel); ok {
						return parameter.Type, nil
					}
					return nil, nil
				},
			},
		},
	})
}