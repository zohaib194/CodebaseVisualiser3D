// Package util contains supplimentary functionality like logging
package util

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"strings"
	"testing"
)

var out bytes.Buffer
var tLogger typeLogger

type args struct {
	format string
	v      []interface{}
}

var tests = []struct {
	name                  string
	args                  args
	expectedMessageValues []string
}{
	// Valid cases
	{
		name: "Valid_Print",
		args: args{
			format: "%s: Received request for repository list",
			v: []interface{}{
				"123123123",
			},
		},
		expectedMessageValues: []string{
			"Received request for repository list",
			"123123123",
		},
	},
	{
		name: "Valid_Print",
		args: args{
			format: "hello \n world, im a programmer from %s",
			v: []interface{}{
				"NTNU",
			},
		},
		expectedMessageValues: []string{
			"hello \n world, im a programmer from NTNU",
		},
	},
	// Invalid cases
	{
		name: "Invalid_Print",
		args: args{
			format: "%08x %08x %08x %08x %08x %n",
			v: []interface{}{
				"%08x %08x %08x %08x %08x %n",
			},
		},
		expectedMessageValues: []string{
			" %!x(MISSING)",
			" %!x(MISSING)",
			" %!x(MISSING)",
			" %!x(MISSING)",
			" %!n(MISSING)",
		},
	},
	{
		name: "Invalid_Print",
		args: args{
			format: "王明是中国人 %s",
			v: []interface{}{
				"中国明是中人王明王国人",
			},
		},
		expectedMessageValues: []string{
			"王明是中国人 中国明是中人王明王国人",
		},
	},
}

func TestMain(m *testing.M) {
	tLogger.logger = log.New(&out, "", log.LstdFlags)

	os.Exit(m.Run())
}

func checkSubstrings(str string, subs []string) (bool, int) {
	matches := 0
	isCompleteMatch := true

	for _, sub := range subs {
		if strings.Contains(str, sub) {
			matches++
		} else {
			isCompleteMatch = false
		}
	}

	return isCompleteMatch, matches
}

func Test_typeLogger_Debug(t *testing.T) {
	tLogger.level = logDebug

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tLogger.Debug(tt.args.format, tt.args.v...)
		})

		expected := fmt.Sprintf(tt.args.format, tt.args.v...)
		completeMatch, _ := checkSubstrings(out.String(), tt.expectedMessageValues)
		if completeMatch != true {
			t.Errorf("Debug() = %v, want values in output: %v", out.String(), expected)
		}
	}
}

func Test_typeLogger_Info(t *testing.T) {
	tLogger.level = logInfo

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tLogger.Info(tt.args.format, tt.args.v...)
		})

		expected := fmt.Sprintf(tt.args.format, tt.args.v...)
		completeMatch, _ := checkSubstrings(out.String(), tt.expectedMessageValues)
		if completeMatch != true {
			t.Errorf("Info() = %v, want values in output: %v", out.String(), expected)
		}
	}
}

func Test_typeLogger_Warn(t *testing.T) {
	tLogger.level = logWarning

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tLogger.Warn(tt.args.format, tt.args.v...)
		})

		expected := fmt.Sprintf(tt.args.format, tt.args.v...)
		completeMatch, _ := checkSubstrings(out.String(), tt.expectedMessageValues)
		if completeMatch != true {
			t.Errorf("Info() = %v, want values in output: %v", out.String(), expected)
		}
	}
}

func Test_typeLogger_Error(t *testing.T) {
	tLogger.level = logError

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tLogger.Error(tt.args.format, tt.args.v...)
		})

		expected := fmt.Sprintf(tt.args.format, tt.args.v...)
		completeMatch, _ := checkSubstrings(out.String(), tt.expectedMessageValues)
		if completeMatch != true {
			t.Errorf("Info() = %v, want values in output: %v", out.String(), expected)
		}
	}
}

func TestSetLogLevel(t *testing.T) {
	type args struct {
		level string
	}
	tests := []struct {
		name string
		args args
		want bool
	}{
		// Valid test cases.
		{
			name: "Valid_LogDebug_Level",
			args: args{
				level: LogDebug,
			},
			want: true,
		},
		{
			name: "Valid_LogInfo_Level",
			args: args{
				level: LogInfo,
			},
			want: true,
		},
		{
			name: "Valid_LogWarning_Level",
			args: args{
				level: LogWarning,
			},
			want: true,
		},
		{
			name: "Valid_LogError_Level",
			args: args{
				level: LogError,
			},
			want: true,
		},
		// Invalid test cases.
		{
			name: "Invalid_Log_Level",
			args: args{
				level: "Lorem Ipsum",
			},
			want: false,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := SetLogLevel(tt.args.level); got != tt.want {
				t.Errorf("SetLogLevel() = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestSetLogFile(t *testing.T) {
	file := "testlogfile"
	tests := []struct {
		name     string
		filepath string
		want     bool
	}{
		// Valid test cases.
		{
			name:     "./Valid_LogFile",
			filepath: file,
			want:     true,
		},
		// Invalid test cases.
		{
			name:     "Invalid_FilePath_LogFile",
			filepath: "./TestLog/Lorem Ipsum",
			want:     false,
		},
		{
			name:     "Invalid_Empty_LogFile",
			filepath: "",
			want:     false,
		},
		{
			name:     "Invalid_Path_LogFile",
			filepath: "./Hey/",
			want:     false,
		},
	}

	// Create the file for test.
	err := ioutil.WriteFile(file, []byte(""), 0666)
	if err != nil {
		TypeLogger.Error("Could not create testlogfile : %s", err.Error())
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := SetLogFile(tt.filepath); got != tt.want {
				t.Errorf("SetLogFile() = %v, want %v", got, tt.want)
			}
		})
	}

	err = os.Remove(file)
	if err != nil {
		TypeLogger.Error("Could not delete testlogfile : %s", err.Error())
	}
}
