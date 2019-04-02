package model

// FunctionModel represents code for a single function
type FunctionModel struct {
	Name         string            `json:"name"`
	DeclID       string            `json:"declrator_id"`
	FunctionBody FunctionBodyModel `json:"function_body, omitempty"`
	Parameters   []ParameterModel `json:"parameters, omitempty"`
	Scope        string            `json:"scope, omitempty"`
	StartLine    int               `json:"start_line"`
	EndLine      int               `json:"end_line"`
}
