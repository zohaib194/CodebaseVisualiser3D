package model

// NamespaceModel represents code for a single namespace
type NamespaceModel struct {
	NamespaceName string            `json:"name"`
	Functions     []FunctionModel  `json:"functions, omitempty"`
	Namespaces    []NamespaceModel `json:"namespaces, omitempty"`
	Classes       []ClassModel    `json:"classes, omitempty"`
	Variables     []VariableModel  `json:"variables, omitempty"`
}
