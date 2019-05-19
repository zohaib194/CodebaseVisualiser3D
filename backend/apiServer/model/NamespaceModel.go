package model

import(
	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
	"fmt"
)

// NamespaceModel represents code for a single namespace
type NamespaceModel struct {
	NamespaceName   string                `json:"name"`
	Functions       []FunctionModel       `json:"functions,omitempty"`
	Namespaces      []NamespaceModel      `json:"namespaces,omitempty"`
	UsingNamespaces []UsingNamespaceModel `json:"using_namespaces,omitempty"`
	Includes        []string              `json:"includes,omitempty"`
	Classes         []ClassModel          `json:"classes,omitempty"`
	Variables       []VariableModel       `json:"variables,omitempty"`
}

func GetNamespaceObject() *graphql.Object {
	fmt.Println("GetNamespaceObject")
	util.TypeLogger.Debug("%s: Call for GetNamespaceObject", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for GetNamespaceObject", packageName)

	return graphql.NewObject(graphql.ObjectConfig{
		Name:        "namespace",
		Description: "A namespace.",
		Fields: graphql.Fields{
			"name": &graphql.Field{
				Type: graphql.String,
				Description: "Name of the namespace.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if namespace, ok := p.Source.(NamespaceModel); ok {
						return namespace.NamespaceName, nil
					}
					return nil, nil
				},
			},/*
			"functions": &graphql.Field{
				Type: graphql.NewList(GetFunctionObject()),
				Description: "Functions withing this namespace.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if namespace, ok := p.Source.(NamespaceModel); ok {
						return namespace.Functions, nil
					}
					return nil, nil
				},
			},/*
			"namespaces": &graphql.Field{
				Type: graphql.NewList(GetNamespaceObject()),
				Description: "Namespaces within this namespace.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if namespace, ok := p.Source.(NamespaceModel); ok {
						return namespace.Namespaces, nil
					}
					return nil, nil
				},
			},*/
			"using_namespaces": &graphql.Field{
				Type: graphql.NewList(GetUsingNamespaceObject()),
				Description: "Extractions of namespacces within this namespace.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if namespace, ok := p.Source.(NamespaceModel); ok {
						return namespace.UsingNamespaces, nil
					}
					return nil, nil
				},
			},
			"includes": &graphql.Field{
				Type: graphql.NewList(graphql.String),
				Description: "Includes declared within this namespace.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if namespace, ok := p.Source.(NamespaceModel); ok {
						return namespace.Includes, nil
					}
					return nil, nil
				},
			},
			"classes": &graphql.Field{
				Type: graphql.NewList(GetClassObject()),
				Description: "classes within this namespace.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if namespace, ok := p.Source.(NamespaceModel); ok {
						return namespace.Classes, nil
					}
					return nil, nil
				},
			},
			"variables": &graphql.Field{
				Type: graphql.NewList(GetVariableObject()),
				Description: "Variables within this namespace.",
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					if namespace, ok := p.Source.(NamespaceModel); ok {
						return namespace.Variables, nil
					}
					return nil, nil
				},
			},
		},
	})
}