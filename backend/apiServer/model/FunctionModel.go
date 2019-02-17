package model

type FunctionsModel struct {
	Function FunctionModel `json:"function"`

}

// Function that represents a function from the source code.
type FunctionModel struct {
	Name string `json:"name"`
}
