package model

// Functions represent all the functions in the repository.
type Functions struct {
	Functions []Function `json:"functions"`
}

// Function that represents a function from the source code.
type Function struct {
	File string        `json:"file"`
	Name []interface{} `json:"function_names"`
}
