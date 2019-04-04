package model

// AccessSpecifierModel represents code for a single access specifier
type AccessSpecifierModel struct {
	Name      string          `json:"name"`
	Classes   []ClassModel    `json:"classes,omitempty"`
	Functions []FunctionModel `json:"functions,omitempty"`
	Variables []VariableModel `json:"variables,omitempty"`
}
