package model

// CallModel represents a function call from code.
type CallModel struct {
	Identifier string       `json:"identifier"`
	Scope      []ScopeModel `json:"scopes,omitempty"`
}
