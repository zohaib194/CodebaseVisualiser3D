package model

// ScopeModel represents the scope and name of the function call.
type ScopeModel struct {
	Identifier string `json:"identifier"`
	Type       string `json:"type,omitempty"`
}
