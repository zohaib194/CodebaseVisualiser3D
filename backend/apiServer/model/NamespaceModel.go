package model

// NamespacesModel is a wrapper for NamespaceModel for json parsing
type NamespacesModel struct {
	Namespace NamespaceModel `json:"namespace"`
	LineNr    int            `json:"line_nr"`
}

// NamespaceModel represents code for a single namespace
type NamespaceModel struct {
	NamespaceName string            `json:"name"`
	Functions     []FunctionsModel  `json:"functions"`
	Namespaces    []NamespacesModel `json:"namespaces"`
	Classes       []ClassesModel    `json:"classes"`
	Variables     []VariablesModel  `json:"variables"`
}
