//Package model refers to model part of mvc.
//It performs abstraction of datamodel and encapsulates save and retrieves.
package model

import (
	"reflect"
	"testing"
	"os"
	"fmt"
	"log"
	"io/ioutil"
	"path/filepath"
	"strings"
	"errors"

	"gopkg.in/mgo.v2/bson"
)

var validRepo = RepoModel{URI: "https://github.com/zohaib194/CodebaseVisualizer3D.git"}
var testFileCount = 10;

type testFile struct {
	relativePath string
	content      string
}

func TestMain(m *testing.M) {

	setup()

	exitCode := m.Run()

	tearDown()

	os.Exit(exitCode)
}

func TestRepoModel_Save(t *testing.T) {
	type fields struct {
		URI string
		ID  bson.ObjectId
	}
	type args struct {
		c chan SaveResponse
	}
	type expect struct{
		responses []SaveResponse
	}
	tests := []struct {
		name   string
		fields fields
		args   args
		expect expect
	}{
		{
			name: "Valid repo",
			fields: fields{
				URI:getCurrentGitRepoPath(),
			},
			args: args{
				c: make(chan SaveResponse),
			},
			expect: expect{
				responses: []SaveResponse{
					{
						ID: "NEW",
						StatusText: "Cloning",
						Err: nil,

					},{
						ID: "GIVEN",
						StatusText: "Done",
						Err: nil,
					},
				},
			},
		},{
			name: "Conflicting repo",
			fields: fields{
				URI:getCurrentGitRepoPath(),
			},
			args: args{
				c: make(chan SaveResponse),
			},
			expect: expect{
				responses: []SaveResponse{
					{
						ID: "NEW",
						StatusText: "Failed",
						Err: errors.New("Already exists"),
					},
				},
			},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			repo := RepoModel{
				URI: tt.fields.URI,
				ID:  tt.fields.ID,
			}
			go repo.Save(tt.args.c)

			var saverResponse SaveResponse
			var id = ""
			for i := 0; i < len(tt.expect.responses); i++ {
				saverResponse = <- tt.args.c

				switch tt.expect.responses[i].ID {
				case "NEW":
					id = saverResponse.ID

				case "GIVEN":
					if id == "" || id != saverResponse.ID {
						t.Errorf("Unexpected ID, got %s expected %s", saverResponse.ID, id)
					}

				default:
					if id != saverResponse.ID {
						t.Errorf("Unexpected ID, got %s expected %s", saverResponse.ID, id)
					}
				}


				if tt.expect.responses[i].StatusText != saverResponse.StatusText{
					t.Errorf("Unexpected response, got %v expected %v", saverResponse, tt.expect.responses[i])
				}

				// Check if either error is nil and if so, if they are both nil.
				if (tt.expect.responses[i].Err == nil || saverResponse.Err == nil) && (tt.expect.responses[i].Err != saverResponse.Err) {
					t.Errorf("Unexpected error, got %v expected %v", saverResponse, tt.expect.responses[i])

				} else {
					if tt.expect.responses[i].Err != nil && saverResponse.Err != nil {
						if tt.expect.responses[i].Err.Error() != saverResponse.Err.Error() {
							t.Errorf("Unexpected response, got %v expected %v", saverResponse, tt.expect.responses[i])
						}
					}
				}
			}

		})
	}
}

func TestRepoModel_Load(t *testing.T) {
	log.Println("Load")
	type fields struct {
		URI string
		ID  bson.ObjectId
	}
	type args struct {
		file   string
		target string
	}
	tests := []struct {
		name     string
		fields   fields
		args     args
		wantData FilesModel
		wantErr  bool
	}{
		{
			name: "Valid textFile",
			fields: fields{
				URI: validRepo.URI,
				ID: validRepo.ID,
			},
			args: args{
				file: fmt.Sprintf("/tmp/%s/subfolder/cppFile.cpp", validRepo.ID.Hex()),
				target: "cpp",
			},
			wantData: FilesModel{
				File: FileModel{
					Parsed: true,
					FileName: fmt.Sprintf("/tmp/%s/subfolder/cppFile.cpp", validRepo.ID.Hex()),
					Functions: []FunctionsModel{
						{
							Function: FunctionModel{
								Name: "int main()",
								DeclID: "main",
								FunctionBody: FunctionBodyModel{
									Variables: []VariablesModel{
										{
											VariableModel{
												Name: "i",
												Type: "int",
											},
										},
									},
									Calls: []string{},
								},
								StartLine: 2,
								EndLine: 4,
							},
						},
					},
					LinesInFile: 4,
				},
			},
		},{
			name: "Invalid textFile",
			fields: fields{
				URI: validRepo.URI,
				ID: validRepo.ID,
			},
			args: args{
				file: fmt.Sprintf("/tmp/%s/subfolder/nonexistentcppFile.cpp", validRepo.ID.Hex()),
				target: "cpp",
			},
			wantData: FilesModel{
				File:FileModel{
					Parsed: false,
					FileName: fmt.Sprintf("/tmp/%s/subfolder/nonexistentcppFile.cpp", validRepo.ID.Hex()),
				},
			},
			wantErr: true,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			repo := RepoModel{
				URI: tt.fields.URI,
				ID:  tt.fields.ID,
			}
			gotData, err := repo.Load(tt.args.file, tt.args.target)
			if (err != nil) != tt.wantErr {
				t.Errorf("RepoModel.Load() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if !reflect.DeepEqual(gotData, tt.wantData) {
				t.Errorf("RepoModel.Load() = %v, want %v", gotData, tt.wantData)
			}
		})
	}
}

func TestRepoModel_GetRepoByID(t *testing.T) {
	log.Println("GetRepoByID")
	type fields struct {
		URI string
		ID  bson.ObjectId
	}
	type args struct {
		id string
	}
	tests := []struct {
		name    string
		fields  fields
		args    args
		wantRep RepoModel
		wantErr bool
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			repo := RepoModel{
				URI: tt.fields.URI,
				ID:  tt.fields.ID,
			}
			gotRep, err := repo.GetRepoByID(tt.args.id)
			if (err != nil) != tt.wantErr {
				t.Errorf("RepoModel.GetRepoByID() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if !reflect.DeepEqual(gotRep, tt.wantRep) {
				t.Errorf("RepoModel.GetRepoByID() = %v, want %v", gotRep, tt.wantRep)
			}
		})
	}
}

func TestRepoModel_GetRepoFiles(t *testing.T) {
	log.Println("GetRepoFiles")
	type fields struct {
		URI string
		ID  bson.ObjectId
	}
	tests := []struct {
		name      string
		fields    fields
		wantFiles string
		wantErr   bool
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			repo := RepoModel{
				URI: tt.fields.URI,
				ID:  tt.fields.ID,
			}
			gotFiles, err := repo.GetRepoFiles()
			if (err != nil) != tt.wantErr {
				t.Errorf("RepoModel.GetRepoFiles() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if gotFiles != tt.wantFiles {
				t.Errorf("RepoModel.GetRepoFiles() = %v, want %v", gotFiles, tt.wantFiles)
			}
		})
	}
}

func TestRepoModel_SanitizeFilePaths(t *testing.T) {
	log.Println("SanitizeFilePaths")
	type fields struct {
		URI string
		ID  bson.ObjectId
	}
	type args struct {
		projectModel ProjectModel
	}
	tests := []struct {
		name   string
		fields fields
		args   args
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			repo := RepoModel{
				URI: tt.fields.URI,
				ID:  tt.fields.ID,
			}
			repo.SanitizeFilePaths(tt.args.projectModel)
		})
	}
}

func TestRepoModel_ParseDataFromFiles(t *testing.T) {
	log.Println("ParseDataFromFiles")
	type fields struct {
		URI string
		ID  bson.ObjectId
	}
	type args struct {
		files             string
		responsePerNFiles int
		c                 chan ParseResponse
	}
	tests := []struct {
		name   string
		fields fields
		args   args
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			repo := RepoModel{
				URI: tt.fields.URI,
				ID:  tt.fields.ID,
			}
			repo.ParseDataFromFiles(tt.args.files, tt.args.responsePerNFiles, tt.args.c)
		})
	}
}

func TestRepoModel_FetchAll(t *testing.T) {
	log.Println("FetchAll")
	type fields struct {
		URI string
		ID  bson.ObjectId
	}
	tests := []struct {
		name           string
		fields         fields
		wantRepoModels []bson.M
		wantErr        bool
	}{
		// TODO: Add test cases.
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			repo := RepoModel{
				URI: tt.fields.URI,
				ID:  tt.fields.ID,
			}
			gotRepoModels, err := repo.FetchAll()
			if (err != nil) != tt.wantErr {
				t.Errorf("RepoModel.FetchAll() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if !reflect.DeepEqual(gotRepoModels, tt.wantRepoModels) {
				t.Errorf("RepoModel.FetchAll() = %v, want %v", gotRepoModels, tt.wantRepoModels)
			}
		})
	}
}

// newTestFile creates a testfile with description of usecase and lorem ipsum text.
func newTestFile(filepath string, useCase string) testFile {

	file := testFile{
		relativePath: filepath,
		content: fmt.Sprintf(`Testfile for repo_test.
		Repo from package model deals with aquiering and storing information about repositories.
		%s
		Vitae et incidunt temporibus est facere eos qui reiciendis. Voluptatum ipsum corrupti possimus incidunt porro quam minus eos. Quisquam similique minima minima. Non repellendus omnis atque deserunt dolorum est molestiae. Quidem quis perspiciatis quae et minus in optio.
		Voluptatum reprehenderit sapiente quae saepe. Ut rerum et est maxime sit aut dicta blanditiis. In molestias eos rerum pariatur molestiae.
		Quia rerum sunt nam atque quia. Dignissimos molestiae dolor esse vitae. Et sit placeat nisi in qui. Ipsum ipsum quos ullam dolore nisi consequuntur porro alias.
		Excepturi officia consequatur placeat. Natus aliquid est pariatur est est. Omnis quo vitae aliquam nisi. Ut asperiores dolor est.
		Qui laborum repudiandae dignissimos. Voluptate maiores error eum fugit voluptatem nihil. Facere dignissimos mollitia harum ullam. Sequi quis dolorem ut ipsam consequatur enim enim. Blanditiis provident minima accusantium libero id.`, useCase),
	}
	return file
}

func newCppFile(filepath string) testFile {

	file :=testFile{
		relativePath:filepath,
		content: `
		int main(){
			int i;
		}
		`,
	}

	return file
}

// setup Sets variables used for storing repository files and initialize the database for testing
func setup() {
	DB.DatabaseName = "test"
	RepoPath = "/tmp/"
	JavaParserPath="/home/Flero/go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/parser/build/classes/java/main"


	if err := DB.Init(); err != nil {
		log.Fatalf("Could not initialize database, database error: %s", err.Error())
	}

	if err := DB.Add(&validRepo); err != nil {
		log.Fatalf("Could not setup test environment, datbase add error: %s", err.Error())
	}

	os.Mkdir("/tmp/"+validRepo.ID.Hex(), os.ModePerm)

	var rootFileCount = testFileCount - 2;
	for i := 0; i < rootFileCount; i++ {
		newFile := newTestFile(fmt.Sprintf("file%d.test", i+1), fmt.Sprintf("File nr %d out of %d total files in root folder", i+1, rootFileCount))
		d := []byte(newFile.content)
		if err := ioutil.WriteFile(fmt.Sprintf("/tmp/%s/%s", validRepo.ID.Hex(), newFile.relativePath), d, 0644); err != nil {
			log.Printf("Could not setup test environment, error writing file: %s", err.Error())

			tearDown()
			os.Exit(1)
		}
	}

	os.Mkdir("/tmp/"+validRepo.ID.Hex()+"/subfolder", os.ModePerm)
	newFile := newTestFile("subfolder/file.test", "File in sub folder")
	d := []byte(newFile.content)
	if err := ioutil.WriteFile(fmt.Sprintf("/tmp/%s/%s", validRepo.ID.Hex(), newFile.relativePath), d, 0644); err != nil {
		log.Printf("Could not setup test environment, error writing file: %s", err.Error())

		tearDown()
		os.Exit(1)
	}

	newCppFile := newCppFile("/subfolder/cppFile.cpp")
	d = []byte(newCppFile.content)
	if err := ioutil.WriteFile(fmt.Sprintf("/tmp/%s/%s", validRepo.ID.Hex(), newCppFile.relativePath), d, 0644); err != nil {
		log.Printf("Could not setup test environment, error writing file: %s", err.Error())

		tearDown()
		os.Exit(1)
	}
}

// tearDown reverts changes done by setup
func tearDown() {
	if err := DB.DropDB(); err != nil {
		log.Println("Could not clean up test database")
	}

	if err := os.RemoveAll("/tmp/" + validRepo.ID.Hex()); err != nil {
		log.Println("Could not clean up repository example file")
	}
}

// getCurrentGitRepoPath finds the folder CodebaseVisualizer3D from executables path
func getCurrentGitRepoPath() string {
	dir, err := filepath.Abs(".")
	if err != nil{
		tearDown()
		log.Fatal("Could not get current path")
	}

	index := strings.Index(dir, "CodebaseVisualizer3D")

	// If Expected foldername was not found
	if index == -1{
		tearDown()
		log.Fatal("Not within CodebaseVisualizer3D git repository")
	}

	return dir[0:index+len("CodebaseVisualizer3D")]
}