package model

import(
	"github.com/graphql-go/graphql"
)

// ProjectModel represent the codebase in a repository
type ProjectModel struct {
	Files []FileModel `json:"files"`
}

func GetProjectObject() *graphql.Object {

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "project",
		Description: "A codeing project.",
		Fields: graphql.Fields{
			"files": &graphql.Field{
				Type:         graphql.NewList(GetFileObject()),
				Description: "The project files.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if project, ok := p.Source.(ProjectModel); ok {
						return project.Files, nil
					}
					return nil, nil
				},
			},
		},
	})
}