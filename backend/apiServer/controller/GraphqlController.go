//Package controller refers to controll part of mvc.
//It performs validation, errorhandling and buisness logic
package controller

import (
	"net/http"
	"encoding/json"

	"github.com/graphql-go/graphql"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/model"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
)

// GaphqlController handles graphql queries.
var GraphqlController = newGraphqlController()

// graphqlController handler for graphql queries.
type graphqlController struct {
	schema graphql.Schema
}

// graphqlQueryString for handling json query request
type graphqlQueryString struct {
	Query string `json:"query"`
	Variables map[string]interface{} `json:"variables"`
	OperationName string `json:"operationName"`

}

func newGraphqlController() graphqlController {
	util.TypeLogger.Debug("%s: Call for newGraphqlController", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for newGraphqlController", packageName)

	queryType := graphql.NewObject(graphql.ObjectConfig{
		Name: "Query",
		Fields: graphql.Fields{
			"repo": &graphql.Field{
				Type: model.GetRepositoryObject(),
				Args: graphql.FieldConfigArgument{
					"id": &graphql.ArgumentConfig{
						Description: "Bison ID for repository",
						Type: graphql.NewNonNull(graphql.String),
					},
				},
				Resolve: func(p graphql.ResolveParams) (interface{}, error) {
					repo, err := model.RepoModel{}.GetRepoByID(p.Args["id"].(string))

					return repo, err
				},
			},
		},
	})

	schema, err := graphql.NewSchema(graphql.SchemaConfig{
		Query: queryType,
	})
	if err != nil {
		util.TypeLogger.Fatal("failed to setup controller for graphql. Schema error: %v", err)
	}

	return graphqlController{
		schema: schema,
	}

}

// Example returns the field hello with content world if requested
func (gql graphqlController) Example(w http.ResponseWriter, r *http.Request) {
	util.TypeLogger.Info("%s: Received request for implementation", packageName)
	defer util.TypeLogger.Info("%s: Ended request for implementation", packageName)

	http.Header.Add(w.Header(), "content-type", "application/json")
	http.Header.Add(w.Header(), "Access-Control-Allow-Origin", "*")

	if r.Method == "POST" {

		// Query
		decoder := json.NewDecoder(r.Body)
		var query graphqlQueryString

		if err := decoder.Decode(&query); err != nil {
			util.TypeLogger.Error("%s: failed to decode json for query: %s", packageName, err.Error())
			http.Error(w, http.StatusText(http.StatusBadRequest), http.StatusBadRequest)
			return
		}
		util.TypeLogger.Debug("Value of variables: %v", query.Variables)
		params := graphql.Params{Schema: gql.schema, RequestString: query.Query, VariableValues: query.Variables}
		result := graphql.Do(params)

		if len(result.Errors) > 0 {
			util.TypeLogger.Error("failed to execute graphql operation, errors: %+v. Query: %v", result.Errors, query.Query)
			http.Error(w, http.StatusText(http.StatusBadRequest), http.StatusBadRequest)
		}

		w.WriteHeader(http.StatusOK)
		json.NewEncoder(w).Encode(result)

	} else { // if not POST request
		http.Error(w, http.StatusText(http.StatusBadRequest), http.StatusBadRequest)
		return
	}

}
