package model

import (
	"testing"

	"gopkg.in/mgo.v2"
)

//
func setupDB(t *testing.T) *MongoDB {

	db := MongoDB {
		DatabaseURL:  "mongodb://localhost",
		DatabaseName: "TestDB",
		RepoColl:   "gitRepositoryTest",
	}

	session, err := mgo.Dial(db.DatabaseURL)
	defer session.Close()

	if err != nil {
		t.Error(err)
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
		name    string
		args    args
		wantErr bool
		wantErrMessage string
		expectedCount int
	}{
		// Valid cases
		{
			name:"Valid_new_repo",
			args: args {
				rm: &RepoModel{
					URI: "www.example.com",
				},
			},
			wantErr: false,
			wantErrMessage: "",
			expectedCount: 1,
		},
		// Invalid cases
		{
			name:"inValid_existing_repo",
			args: args {
				rm: &RepoModel{
					URI: "www.example.com",
				},
			},
			wantErr: true,
			wantErrMessage: "Already exists",
			expectedCount: 1,
		},
		{
			name:"Invalid_new_repo",
			args: args {
				rm: &RepoModel{
					URI: "",
				},
			},
			wantErr: true,
			wantErrMessage: "URI is empty",
			expectedCount: 1,
		},
	}

	db.Init()
	if db.Count() != 0 {
		t.Errorf("Database not properly initialized, gitRepositoryTest count should be 0 got %v", db.Count())
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {

			err := db.Add(tt.args.rm);
			if (err != nil) != tt.wantErr {
				t.Errorf("MongoDB.Add() error = %v, wantErr %v", err, tt.wantErr)
			}

			if tt.wantErr && err.Error() != tt.wantErrMessage {
				t.Errorf("MongoDB.Add() errorMessage = %v, wantErrMessage %v", err.Error(), tt.wantErrMessage)
			}

			if(db.Count() != tt.expectedCount){
				t.Errorf("gitRepositoryTest count should be 1 got %v", db.Count())
			}
		})
	}
}
