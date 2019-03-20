package model

// VariablesModel is wrapper for VariableModel for json parsing.
type VariablesModel struct {
	Variable VariableModel `json:"variable"`
}

// VariableModel represents a variable from code.
type VariableModel struct {
	Name string `json:"name"`
	Type string `json:"type"`
}
