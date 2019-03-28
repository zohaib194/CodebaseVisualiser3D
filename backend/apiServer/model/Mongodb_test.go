package model

import (
	"testing"

	"gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

// Setup the database and returns db
func setupDB(t *testing.T) *MongoDB {

	db := MongoDB{
		DatabaseURL:  "mongodb://localhost",
		DatabaseName: "TestDB",
		RepoColl:     "gitRepositoryTest",
	}

	session, err := mgo.Dial(db.DatabaseURL)
	defer session.Close()

	if err != nil {
		t.Errorf("Could not dial the database: %s", err.Error())
	}
	return &db
}

func TestMongoDB_Add(t *testing.T) {
	db := setupDB(t)
	defer db.DropDB()

	type args struct {
		rm *RepoModel
	}
	tests := []struct {
		name           string
		args           args
		wantErr        bool
		wantErrMessage string
		expectedCount  int
	}{
		// Valid cases
		{
			name: "Valid_new_repo",
			args: args{
				rm: &RepoModel{
					URI: "www.example.com",
				},
			},
			wantErr:        false,
			wantErrMessage: "",
			expectedCount:  1,
		},
		// Invalid cases
		{
			name: "inValid_existing_repo",
			args: args{
				rm: &RepoModel{
					URI: "www.example.com",
				},
			},
			wantErr:        true,
			wantErrMessage: "Already exists",
			expectedCount:  1,
		},
		{
			name: "Invalid_new_repo",
			args: args{
				rm: &RepoModel{
					URI: "",
				},
			},
			wantErr:        true,
			wantErrMessage: "URI is empty",
			expectedCount:  1,
		},
	}

	if err := db.Init(); err != nil {
		t.Errorf("Could not initialize database, database error: %s", err.Error())
	}
	if db.Count() != 0 {
		t.Errorf("Database not properly initialized, gitRepositoryTest count should be 0 got %v", db.Count())
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {

			err := db.Add(tt.args.rm)
			if (err != nil) != tt.wantErr {
				t.Errorf("MongoDB.Add() error = %v, wantErr %v", err, tt.wantErr)
			}

			if tt.wantErr && err.Error() != tt.wantErrMessage {
				t.Errorf("MongoDB.Add() errorMessage = %v, wantErrMessage %v", err.Error(), tt.wantErrMessage)
			}

			if db.Count() != tt.expectedCount {
				t.Errorf("gitRepositoryTest count should be 1 got %v", db.Count())
			}
		})
	}
}

func TestMongoDB_findRepoByURI(t *testing.T) {
	db := setupDB(t)
	defer db.DropDB()

	type args struct {
		uri string
	}

	tests := []struct {
		name     string
		args     args
		addRepo  RepoModel
		wantRepo RepoModel
		wantErr  bool
	}{
		// Valid cases
		{
			name: "Valid_uri_tofind",
			args: args{
				uri: "www.example.com",
			},
			addRepo: RepoModel{
				URI: "www.example.com",
			},
			wantRepo: RepoModel{
				URI: "www.example.com",
			},
			wantErr: false,
		},
		// Invalid cases.
		{
			name: "inValid_uri_tofind",
			args: args{
				uri: "123",
			},
			addRepo: RepoModel{
				URI: "www.example123.com",
			},
			wantRepo: RepoModel{
				URI: "",
			},
			wantErr: false,
		},
	}

	if err := db.Init(); err != nil {
		t.Errorf("Could not initialize database, database error: %s", err.Error())
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {

			db.Add(&tt.addRepo)
			gotRepo, err := db.findRepoByURI(tt.args.uri)
			if (err != nil) != tt.wantErr {
				t.Errorf("MongoDB.findRepoByURI() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if gotRepo.ID != tt.wantRepo.ID && gotRepo.URI != tt.wantRepo.URI {
				t.Errorf("MongoDB.findRepoByURI() = %v, want %v", gotRepo, tt.wantRepo)
			}
		})
	}
}

func TestMongoDB_FindRepoByID(t *testing.T) {
	db := setupDB(t)
	defer db.DropDB()

	type args struct {
		id string
	}
	tests := []struct {
		name                 string
		args                 args
		addRepo              RepoModel
		wantRepo             RepoModel
		wantErr              bool
		expectedErrorMessage string
	}{
		// Valid cases
		{
			name: "Valid_uri_tofind",
			addRepo: RepoModel{
				URI: "www.example.com",
			},
			wantRepo: RepoModel{
				URI: "www.example.com",
			},
			wantErr:              false,
			expectedErrorMessage: "",
		},
		// Invalid cases.
		{
			name: "inValid_uri_tofind",
			args: args{
				id: "123123",
			},
			addRepo: RepoModel{
				URI: "www.example123.com",
			},
			wantRepo: RepoModel{
				URI: "",
			},
			wantErr:              true,
			expectedErrorMessage: "Invalid id",
		},
	}

	if err := db.Init(); err != nil {
		t.Errorf("Could not initialize database, database error: %s", err.Error())
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {

			db.Add(&tt.addRepo)
			if !tt.wantErr {
				tt.args.id = tt.addRepo.ID.Hex()
			}

			gotRepo, err := db.FindRepoByID(tt.args.id)
			if (err != nil) != tt.wantErr && err.Error() != tt.expectedErrorMessage {
				t.Errorf("MongoDB.FindRepoByID() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if gotRepo.URI != tt.wantRepo.URI {
				t.Errorf("MongoDB.findRepoByURI() = %v, want %v", gotRepo, tt.wantRepo)
			}
		})
	}
}

func TestMongoDB_FindAllURI(t *testing.T) {

	tests := []struct {
		name      string
		addRepos  []RepoModel
		wantRepos []bson.M
		wantErr   bool
	}{
		// Valid cases.
		{
			name: "Valid_findAllURI",
			addRepos: []RepoModel{
				{URI: "www.example.com/.git"},
				{URI: "www.123example123.com/.git"},
			},
			wantRepos: []bson.M{
				{"uri": "www.123example123.com/.git"},
				{"uri": "www.example.com/.git"},
			},
			wantErr: false,
		},
		{
			name:      "Valid_findAllURI",
			addRepos:  []RepoModel{},
			wantRepos: []bson.M{},
			wantErr:   false,
		},
	}

	for _, tt := range tests {
		db := setupDB(t)

		if err := db.Init(); err != nil {
			t.Errorf("Could not initialize database, database error: %s", err.Error())
		}

		for _, repo := range tt.addRepos {
			db.Add(&repo)
		}

		t.Run(tt.name, func(t *testing.T) {

			gotRepos, err := db.FindAllURI()
			if (err != nil) != tt.wantErr {
				t.Errorf("MongoDB.FindAllURI() error = %v, wantErr %v", err, tt.wantErr)
				return
			}

			for index, gotRepo := range gotRepos {
				if gotRepo["uri"] != tt.wantRepos[index]["uri"] {
					t.Errorf("MongoDB.FindAllURI() = %v, want %v", gotRepos, tt.wantRepos)
				}
			}
		})

		db.DropDB()
	}
}
