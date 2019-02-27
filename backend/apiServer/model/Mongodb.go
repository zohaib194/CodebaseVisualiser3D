package model

import (
	"errors"
	"log"

	"gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

// MongoDB stores the details of the DB connection.
type MongoDB struct {
	DatabaseURL  string
	DatabaseName string
	RepoColl     string
}

const logWarn = "[WARNING]: "
const logError = "[ERROR]: "
const logInfo = "[INFO]: "

// DB is a mongo database with name CodeVis3D and collection gitRepository
var DB = &MongoDB{"mongodb://localhost", "CodeVis3D", "gitRepository"}

// Init - initializes the mongoDB database
func (db *MongoDB) Init() error {
	// Setup session with database
	log.Println(logInfo + "Dialing database! Setting up a session!")
	session, err := mgo.Dial(db.DatabaseURL)

	// Check for session error
	if err != nil {
		log.Println(logError + "Failed dialing database")
		return err
	}

	// Set up currency collation indexing
	index := mgo.Index{
		Key:        []string{"uri"},
		Unique:     true,
		DropDups:   true,
		Background: true,
		Sparse:     true,
	}

	// Ensure collation follows the index
	log.Println(logInfo + "Make collection: " + db.RepoColl + "Ensure \"index\"")
	err = session.DB(db.DatabaseName).C(db.RepoColl).EnsureIndex(index)
	if err != nil {
		log.Println(logError + "Failed to ensure \"index\" on collection: " + db.RepoColl + "!")
		return err
	}

	// Postpone closing connection until we return
	defer session.Close()

	log.Println(logInfo + "Initializing successfull!")
	// Nothing bad happened!
	return nil
}

//add adds rm to db if it is not already in it.
func (db *MongoDB) add(rm *RepoModel) error {
	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		log.Fatalln(logError+"Can't connect to database: ", err)
	}
	defer session.Close()

	exstRepo, err := db.findRepoByURI(rm.URI)

	if err != nil {
		return err
	}

	if exstRepo.ID.Valid() {
		return errors.New("Already exists")
	}

	rm.ID = bson.NewObjectId()

	return session.DB(db.DatabaseName).C(db.RepoColl).Insert(rm)
}

// findRepoByURI takes the repo with field uri as given uri.
// It returns empty repo if it is not in db.
func (db *MongoDB) findRepoByURI(uri string) (repo RepoModel, err error) {
	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		log.Fatalln(logError+"Can't connect to database: ", err)
	}
	defer session.Close()

	// Find any match in the database
	err = session.DB(db.DatabaseName).C(db.RepoColl).Find(bson.M{"uri": uri}).One(&repo)

	// Return empty repo and error if error is not trivial
	if err != nil && err.Error() != "not found" {
		return RepoModel{}, err
	}

	return repo, nil
}

// FindRepoByID takes the repo with field id as given id.
// It returns empty repo if it is not in db.
func (db *MongoDB) FindRepoByID(id string) (repo RepoModel, err error) {
	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		log.Fatalln(logError+"Can't connect to database: ", err)
	}
	defer session.Close()

	if !bson.IsObjectIdHex(id) {
		err = errors.New("Invalid id")
		log.Println(logError+"Id received is incorrect: ", err)
		return RepoModel{}, err
	}

	keyID := bson.ObjectIdHex(id)

	// Return empty repo with error if error is not "Not found"
	if err = session.DB(db.DatabaseName).C(db.RepoColl).Find(bson.M{"_id": keyID}).One(&repo); err != nil && err.Error() != "not found" {
		return RepoModel{}, err
	}

	return repo, nil

}

// FindAll finds and returns all the repos stored in DB.
func (db *MongoDB) FindAll() (repos []RepoModel, err error) {
	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		log.Fatalln(logError+"Can't connect to database: ", err)
	}
	defer session.Close()

	// Return empty repos array with error if error is not "Not found"
	if err = session.DB(db.DatabaseName).C(db.RepoColl).Find(nil).All(&repos); err != nil && err.Error() != "not found" {
		return []RepoModel{}, err
	}

	return repos, nil
}
