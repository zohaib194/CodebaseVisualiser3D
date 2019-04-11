package model

// FileWrapperModel is a wrapper for FileModel for json parsing
type FileWrapperModel struct {
	File FileModel `json:"file"`
}

// FileModel represents a single code file
type FileModel struct {
	Parsed      bool             `json:"parsed"`
	FileName    string           `json:"file_name"`
	Functions   []FunctionModel  `json:"functions,omitempty"`
	Namespaces  []NamespaceModel `json:"namespaces,omitempty"`
	Using_Namespaces  []UsingNamespaceModel `json:"using_namespaces,omitempty"`
	Classes     []ClassModel     `json:"classes,omitempty"`
	Variables   []VariableModel  `json:"variables,omitempty"`
	LinesInFile int              `json:"linesInFile"`
}
