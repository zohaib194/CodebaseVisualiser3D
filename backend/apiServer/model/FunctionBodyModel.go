package model

// FunctionBodyModel represents calls and variable from a function.
type FunctionBodyModel struct {
	Calls     []CallModel     `json:"calls,omitempty"`
	Variables []VariableModel `json:"variables,omitempty"`
}
