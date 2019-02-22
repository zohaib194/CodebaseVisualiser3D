package model

// ProjectModel represent the codebase in a repository
type ProjectModel struct {
	Files []FilesModel `json:"files"`
}
