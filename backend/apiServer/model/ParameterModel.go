package model

// ParametersModel is wrapper for ParameterModel for json parsing.
type ParametersModel struct {
	Variable VariableModel `json:"parameter"`
}

// ParameterModel represents a parameter from code.
type ParameterModel struct {
	Name string `json:"name"`
	Type string `json:"type"`
}
