package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
)

// ProjectModel represent the codebase in a repository
type ProjectModel struct {
	Files []FileModel `json:"files"`
}

func GetProjectObject() *graphql.Object {
	util.TypeLogger.Debug("%s: Call for GetProjectObject", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for GetProjectObject", packageName)

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "project",
		Description: "A codeing project.",
		Fields: graphql.Fields{
			"files": &graphql.Field{
				Type:         graphql.NewList(fileObject),
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