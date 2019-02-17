package model

type NamespacesModel struct{

	Namespace NamespaceModel `json:"namespace"`
}

type NamespaceModel struct{

	Functions []FunctionsModel `json:"functions"`
	NamespaceName string `json:"name"`
	Namespaces []NamespacesModel `json:"namespaces"`
	Classes	[]ClassesModel `json:"classes"`
}