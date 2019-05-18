//Package controller refers to controll part of mvc.
//It performs validation, errorhandling and buisness logic
package controller

import (
	"net/http"
	"encoding/json"

	"github.com/graphql-go/graphql"
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
	Variables map[string]string `json:"variables"`
	OperationName string `json:"operationName"`

}

func newGraphqlController() graphqlController {
	util.TypeLogger.Debug("%s: Call for newGraphqlController", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for newGraphqlController", packageName)

	fields := graphql.Fields{
		"hello": &graphql.Field{
			Type: graphql.String,
			Resolve: func(p graphql.ResolveParams) (interface{}, error) {
				return "world", nil
			},
		},
	}

	rootQuery := graphql.ObjectConfig{Name: "RootQuery", Fields: fields}
	schemaConfig := graphql.SchemaConfig{Query: graphql.NewObject(rootQuery)}
	schema, err := graphql.NewSchema(schemaConfig)
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

		params := graphql.Params{Schema: gql.schema, RequestString: query.Query}
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
