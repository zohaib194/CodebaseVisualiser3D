package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
	"fmt"
)

// ClassModel represents code for a single calss
type ClassModel struct {
	Name                  string                 `json:"name"`
	AccessSpecifierModels []AccessSpecifierModel `json:"access_specifiers,omitempty"`
	Parents               []string               `json:"parents,omitempty"`
}

func GetClassObject() *graphql.Object {
	fmt.Println("GetClassObject")
	util.TypeLogger.Debug("%s: Call for GetClassObject", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for GetClassObject", packageName)

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "class",
		Description: "A codeing project.",
		Fields: graphql.Fields{
			"name": &graphql.Field{
				Type:         graphql.String,
				Description: "Name of the class.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if calzz, ok := p.Source.(ClassModel); ok {
						return calzz.Name, nil
					}
					return nil, nil
				},
			},
			"access_specifiers": &graphql.Field{
				Type:         graphql.NewList(GetAccessSpecifierObject()),
				Description: "Access scope of the class.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if calzz, ok := p.Source.(ClassModel); ok {
						return calzz.AccessSpecifierModels, nil
					}
					return nil, nil
				},
			},
			"parents": &graphql.Field{
				Type:         graphql.String,
				Description: "Classes this class extends from.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if calzz, ok := p.Source.(ClassModel); ok {
						return calzz.Parents, nil
					}
					return nil, nil
				},
			},
		},
	})
}