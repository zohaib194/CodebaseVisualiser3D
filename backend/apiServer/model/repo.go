//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"log"
	"os/exec"

	"gopkg.in/mgo.v2/bson"
)

// RepoPath is the path where git repositories are stored.
var RepoPath string

// RepoModel represents metadata for a git repository.
type RepoModel struct {
	BasePath string        `json:"basepath"`               // Folder for repository to be saved in
	URI      string        `json:"uri"`                    // Where the repository was found
	ID       bson.ObjectId `json:"-" bson:"_id,omitempty"` // Folder name where repo is stored
}

// Save saves repo to database and clones repository
func (repo RepoModel) Save() error {

	// Assure repos storage location is set
	if repo.BasePath != "" {
		repo.BasePath = RepoPath
	}

	err := DB.add(&repo)

	if err != nil {
		log.Println("Could not add to database: ", err)
		return err
	}

	log.Println(repo.ID)

	// Clone repository into storage location with name given by database
	cmd := exec.Command("git", "-C", RepoPath, "clone", repo.URI, repo.ID.Hex())
	_, err = cmd.Output() // TODO: Validate that git clone went well and prevent request for rsa password

	return err
}
