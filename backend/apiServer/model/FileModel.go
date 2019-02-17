package model

type FilesModel struct{
	File FileModel `json:"file"`
}

type FileModel struct{

	FileName string `json:"file_name"`
	Functions []FunctionsModel `json:"functions"`
	Namespaces []NamespacesModel `json:"namespaces"`
	Classes	[]ClassesModel `json:"classes"`
}