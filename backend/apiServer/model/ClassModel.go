package model

// ClassesModel is a wrapper for ClassModel for json parsing
type ClassesModel struct{
	Class ClassModel 	`json:"class"`
}


// ClassModel represents code for a single calss 
type ClassModel struct {
    Name string			`json:"name"`
    Private []string 	`json:"private"`
    Public []string 	`json:"public"`
    Protected []string	`json:"protected`
}
