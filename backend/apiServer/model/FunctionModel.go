package model

// FunctionsModel is a wrapper for FunctionModel for json parsing
type FunctionsModel struct {
	Function FunctionModel `json:"function"`

}

// FunctionModel represents code for a single function 
type FunctionModel struct {
	Name string `json:"name"`
}
