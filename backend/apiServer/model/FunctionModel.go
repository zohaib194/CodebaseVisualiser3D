package model

// FunctionsModel is a wrapper for FunctionModel for json parsing
type FunctionsModel struct {
	Function FunctionModel `json:"function"`
}

// FunctionModel represents code for a single function
type FunctionModel struct {
	Name         string            `json:"name"`
	DeclID       string            `json:"declrator_id"`
	FunctionBody FunctionBodyModel `json:"function_body"`
	Parameters   []ParametersModel `json:"parameters"`
	Scope        string            `json:"scope"`
	StartLine    int               `json:"start_line"`
	EndLine      int               `json:"end_line"`
}
