package model

// VariableModel represents a variable from code.
type CallModel struct {
	Identifier string `json:"identifier"`
	Scope []string `json:"scopes, omitempty"`
}
