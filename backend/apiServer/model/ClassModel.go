package model

type ClassesModel struct{
	Class ClassModel 	`son:"class"`
}


type ClassModel struct {
    Name string			`json:"name"`
    Private []string 	`json:"private"`
    Public []string 	`json:"public"`
    Protected []string	`json:"protected`
}