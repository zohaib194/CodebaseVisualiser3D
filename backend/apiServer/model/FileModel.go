package model

// FilesModel is a wrapper for FileModel for json parsing
type FilesModel struct {
	File FileModel `json:"file"`
}

// FileModel represents a single code file
type FileModel struct {
	Parsed      bool              `json:"parsed"`
	FileName    string            `json:"file_name"`
	Functions   []FunctionsModel  `json:"functions"`
	Namespaces  []NamespacesModel `json:"namespaces"`
	Classes     []ClassesModel    `json:"classes"`
	Variables   []VariablesModel  `json:"variables"`
	LinesInFile int               `json:"linesInFile"`
}
