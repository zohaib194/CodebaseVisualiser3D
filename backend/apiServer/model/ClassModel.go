package model

// ClassModel represents code for a single calss
type ClassModel struct {
	Name                  string                 `json:"name"`
	AccessSpecifierModels []AccessSpecifierModel `json:"access_specifiers,omitempty"`
	Parents               []string               `json:"parents,omitempty"`
}
