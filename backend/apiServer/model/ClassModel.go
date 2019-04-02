package model

// ClassModel represents code for a single calss
type ClassModel struct {
	Name      string   `json:"name"`
	Private   []string `json:"private, omitempty"`
	Public    []string `json:"public, omitempty"`
	Protected []string `json:"protected, omitempty"`
}
