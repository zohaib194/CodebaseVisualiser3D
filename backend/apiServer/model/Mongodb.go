package model

import (
	"errors"

	"gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"

	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/util"
)

var packageName = "model"

// MongoDB stores the details of the DB connection.
type MongoDB struct {
	DatabaseURL  string
	DatabaseName string
	RepoColl     string
}

// DB is a mongo database with name CodeVis3D and collection gitRepository
var DB = &MongoDB{"mongodb://localhost", "CodeVis3D", "gitRepository"}

// Init - initializes the mongoDB database
func (db *MongoDB) Init() error {
	util.TypeLogger.Debug("%s: Call for Init", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for Init", packageName)

	// Setup session with database
	util.TypeLogger.Info("%s: Dialing database, setting up session", packageName)
	session, err := mgo.Dial(db.DatabaseURL)

	// Check for session error
	if err != nil {
		util.TypeLogger.Fatal("%s: Failed to dial database", packageName)
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
	util.TypeLogger.Info("%s: Creating collection %s Ensure \"index\"", packageName, db.RepoColl)
	err = session.DB(db.DatabaseName).C(db.RepoColl).EnsureIndex(index)
	if err != nil {
		util.TypeLogger.Fatal("%s: Failed to ensure \"index\" on collection %s: %s", packageName, db.RepoColl, err.Error())
		return err
	}

	// Postpone closing connection until we return
	defer session.Close()

	util.TypeLogger.Info("%s: Initializing successfull", packageName)
	// Nothing bad happened!
	return nil
}

//Add adds rm to db if it is not already in it.
func (db *MongoDB) Add(rm *RepoModel) error {
	util.TypeLogger.Debug("%s: Call for add", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for add", packageName)

	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		util.TypeLogger.Fatal("%s: Failed to connect to database", packageName)
	}
	defer session.Close()

	if rm.URI == "" {
		return errors.New("URI is empty")
	}

	exstRepo, err := db.findRepoByURI(rm.URI)

	if err != nil {
		return err
	}

	if exstRepo.ID.Valid() {
		rm.ID = exstRepo.ID
		rm.URI = exstRepo.URI
		return errors.New("Already exists")
	}

	rm.ID = bson.NewObjectId()

	return session.DB(db.DatabaseName).C(db.RepoColl).Insert(rm)
}

// findRepoByURI takes the repo with field uri as given uri.
// It returns empty repo if it is not in db.
func (db *MongoDB) findRepoByURI(uri string) (repo RepoModel, err error) {
	util.TypeLogger.Debug("%s: Call for findRepoByURI", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for findRepoByURI", packageName)

	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		util.TypeLogger.Fatal("%s: Failed to connect to database", packageName)
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
	util.TypeLogger.Debug("%s: Call for FindRepoByID", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for FindRepoByID", packageName)

	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		util.TypeLogger.Fatal("%s: Failed to connect to database", packageName)
	}
	defer session.Close()

	if !bson.IsObjectIdHex(id) {
		err = errors.New("Invalid id")
		util.TypeLogger.Fatal("%s: Received incorrect ID", packageName)

		return RepoModel{}, err
	}

	keyID := bson.ObjectIdHex(id)

	// Return empty repo with error if error is not "Not found"
	if err = session.DB(db.DatabaseName).C(db.RepoColl).Find(bson.M{"_id": keyID}).One(&repo); err != nil && err.Error() != "not found" {
		return RepoModel{}, err
	}

	return repo, nil

}

// FindAllURI finds and returns all the repos stored in DB.
func (db *MongoDB) FindAllURI() (repos []bson.M, err error) {
	util.TypeLogger.Debug("%s: Call for FindAllURI", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for FindAllURI", packageName)

	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		util.TypeLogger.Fatal("%s: Failed to connect to database", packageName)
	}
	defer session.Close()

	// Return empty repos array with error if error is not "Not found"
	// aggregate([{$group: {_id: "$_id", uri: {$addToSet: "$uri"}}}])
	if err = session.DB(db.DatabaseName).C(db.RepoColl).Pipe([]bson.M{bson.M{"$group": bson.M{"_id": "$_id", "uri": bson.M{"$first": "$uri"}}}}).All(&repos); err != nil && err.Error() != "not found" {
		return []bson.M{}, err
	}

	return repos, nil
}

// DropDB deletes the database
func (db *MongoDB) DropDB() (err error) {
	util.TypeLogger.Debug("%s: Call for DropDB", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for DropDB", packageName)

	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		util.TypeLogger.Fatal("%s: Failed to connect to database", packageName)
	}
	defer session.Close()

	return session.DB(db.DatabaseName).DropDatabase()
}

// Count returns number of items in the collection.
func (db *MongoDB) Count() int {
	util.TypeLogger.Debug("%s: Call for Count", packageName)
	defer util.TypeLogger.Debug("%s: Ended Call for Count", packageName)

	session, err := mgo.Dial(db.DatabaseURL)
	if err != nil {
		util.TypeLogger.Fatal("%s: Failed to connect to database", packageName)
	}
	defer session.Close()

	count, err := session.DB(db.DatabaseName).C(db.RepoColl).Count()
	if err != nil {
		util.TypeLogger.Fatal("%s: Failed to get db count", packageName)
	}

	return count
}