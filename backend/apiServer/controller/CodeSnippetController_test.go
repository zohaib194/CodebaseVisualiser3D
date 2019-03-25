//Package controller refers to controll part of mvc.
//It performs validation, errorhandling and buisness logic
package controller

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"net/http/httptest"
	"os"
	"strings"
	"testing"

	"github.com/gorilla/mux"
	"github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer/model"
)

var validRepo = model.RepoModel{URI: "https://github.com/zohaib194/CodebaseVisualizer3D.git"}
var client = http.Client{}
var validTestFile = newTestFile()

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

func Test_CodeSnippetController_GetImplementation(t *testing.T) {
	type args struct {
		repoID    string
		lineStart int
		lineEnd   int
		filePath  string
	}
	type expect struct {
		statusCode     int
		implementation string
	}
	tests := []struct {
		name        string
		codeSnippet CodeSnippetController
		args        args
		expected    expect
	}{
		{
			name:        "valid whole file",
			codeSnippet: CodeSnippetController{},
			args: args{
				repoID:    validRepo.ID.Hex(),
				lineStart: 1,
				lineEnd:   7,
				filePath:  validTestFile.relativePath,
			},
			expected: expect{
				statusCode:     200,
				implementation: validTestFile.content,
			},
		}, {
			name:        "valid interval",
			codeSnippet: CodeSnippetController{},
			args: args{
				repoID:    validRepo.ID.Hex(),
				lineStart: 3,
				lineEnd:   5,
				filePath:  validTestFile.relativePath,
			},
			expected: expect{
				statusCode:     200,
				implementation: getInterval(validTestFile.content, 3, 5),
			},
		}, {
			name:        "valid LineEnd greater than EOF",
			codeSnippet: CodeSnippetController{},
			args: args{
				repoID:    validRepo.ID.Hex(),
				lineStart: 1,
				lineEnd:   100,
				filePath:  validTestFile.relativePath,
			},
			expected: expect{
				statusCode:     200,
				implementation: validTestFile.content,
			},
		}, {
			name:        "inValid LineStart less than 1",
			codeSnippet: CodeSnippetController{},
			args: args{
				repoID:    validRepo.ID.Hex(),
				lineStart: 0,
				lineEnd:   7,
				filePath:  validTestFile.relativePath,
			},
			expected: expect{
				statusCode:     400,
				implementation: "",
			},
		}, {
			name:        "inValid LineStart past EOF",
			codeSnippet: CodeSnippetController{},
			args: args{
				repoID:    validRepo.ID.Hex(),
				lineStart: 100,
				lineEnd:   107,
				filePath:  validTestFile.relativePath,
			},
			expected: expect{
				statusCode:     400,
				implementation: "",
			},
		}, {
			name:        "inValid Negative interval",
			codeSnippet: CodeSnippetController{},
			args: args{
				repoID:    validRepo.ID.Hex(),
				lineStart: 7,
				lineEnd:   1,
				filePath:  validTestFile.relativePath,
			},
			expected: expect{
				statusCode:     400,
				implementation: "",
			},
		},
	}

	for _, tt := range tests {
		router := mux.NewRouter()
		router.HandleFunc("/repo/{repoId}/file/read/", tt.codeSnippet.GetImplementation)

		path := fmt.Sprintf("/repo/%s/file/read/?lineStart=%d&lineEnd=%d&filePath=%s/%s", tt.args.repoID, tt.args.lineStart, tt.args.lineEnd, tt.args.repoID, tt.args.filePath)

		t.Run(tt.name, func(t *testing.T) {
			req, err := http.NewRequest("GET", path, nil)
			if err != nil {
				t.Error("Failed to construct new request to server! | Error: " + err.Error())
			}

			resp := httptest.NewRecorder()
			router.ServeHTTP(resp, req)

			if resp.Code != tt.expected.statusCode {
				t.Error("Incorrect statusCode")
			}

			decoder := json.NewDecoder(resp.Body)
			var data map[string]string

			if err := decoder.Decode(&data); err != nil {
				if tt.expected.implementation != "" {
					t.Error("Got no implementation when expected")
				}

			} else if strings.TrimSpace(data["implementation"]) != tt.expected.implementation {
				t.Error("Incorrect implementation")
			}
		})
	}
}

func newTestFile() testFile {

	file := testFile{
		relativePath: "test.test",
		content: `Testfile for CodeSnippetController_test.
		It expects to get the content between two line numbers.
		Vitae et incidunt temporibus est facere eos qui reiciendis. Voluptatum ipsum corrupti possimus incidunt porro quam minus eos. Quisquam similique minima minima. Non repellendus omnis atque deserunt dolorum est molestiae. Quidem quis perspiciatis quae et minus in optio.
		Voluptatum reprehenderit sapiente quae saepe. Ut rerum et est maxime sit aut dicta blanditiis. In molestias eos rerum pariatur molestiae.
		Quia rerum sunt nam atque quia. Dignissimos molestiae dolor esse vitae. Et sit placeat nisi in qui. Ipsum ipsum quos ullam dolore nisi consequuntur porro alias.
		Excepturi officia consequatur placeat. Natus aliquid est pariatur est est. Omnis quo vitae aliquam nisi. Ut asperiores dolor est.
		Qui laborum repudiandae dignissimos. Voluptate maiores error eum fugit voluptatem nihil. Facere dignissimos mollitia harum ullam. Sequi quis dolorem ut ipsam consequatur enim enim. Blanditiis provident minima accusantium libero id.`,
	}
	return file
}

// getInterval gets the substring from linenr start to linenr stop from str, without witespace before or after
func getInterval(str string, start int, stop int) (interval string) {
	intervalSlice := strings.Split(str, "\n")[start-1 : stop]

	for _, line := range intervalSlice {
		interval += line + "\n"
	}

	interval = strings.TrimSpace(interval)

	return interval
}

// setup Sets variables used for storing repository files and initialize the database for testing
func setup() {
	model.DB.DatabaseName = "test"
	model.RepoPath = "/tmp/"

	if err := model.DB.Init(); err != nil {
		log.Fatalf("Could not initialize database, database error: %s", err.Error())
	}

	if err := model.DB.Add(&validRepo); err != nil {
		log.Fatalf("Could not setup test environment, datbase add error: %s", err.Error())
	}

	d1 := []byte(validTestFile.content)
	os.Mkdir("/tmp/"+validRepo.ID.Hex(), os.ModePerm)
	if err := ioutil.WriteFile("/tmp/"+validRepo.ID.Hex()+"/"+validTestFile.relativePath, d1, 0644); err != nil {
		log.Printf("Could not setup test environment, error writing file: %s", err.Error())

		tearDown()
		os.Exit(1)
	}

}

// tearDown reverts changes done by setup
func tearDown() {
	if err := model.DB.DropDB(); err != nil {
		log.Println("Could not clean up test database")
	}

	if err := os.Remove("/tmp/" + validRepo.ID.Hex() + "/test.test"); err != nil {
		log.Println("Could not clean up repository example file")
	}
}
