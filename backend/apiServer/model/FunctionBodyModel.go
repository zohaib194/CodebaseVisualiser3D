package model

// FunctionBodyModel represents calls and variable from a function.
type FunctionBodyModel struct {
	Calls     []string         `json:"calls"`
	Variables []VariablesModel `json:"variables"`
}
