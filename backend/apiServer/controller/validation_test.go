package controller

import (
	"regexp"
	"testing"
)

func Test_validateURI(t *testing.T) {
	type args struct {
		uri             string
		extraValidation func(string) (bool, error)
	}

	tests := []struct {
		name    string
		args    args
		want    bool
		wantErr bool
	}{
		// Valid test cases.
		{
			name: "Valid_URI_Test",
			args: args{
				uri:             "https://github.com/zohaib194/CodebaseVisualizer3D",
				extraValidation: nil,
			},
			want:    true,
			wantErr: false,
		},
		{
			name: "Valid__Prefix_Git_Test",
			args: args{
				uri: "https://github.com/zohaib194/CodebaseVisualizer3D.git",
				extraValidation: func(url string) (isValid bool, err error) {
					return regexp.Match(`\.git$`, []byte(url))
				},
			},
			want:    true,
			wantErr: false,
		},
		// Invalid test cases.
		{
			name: "Invalid_URI_Test",
			args: args{
				uri:             "123123123123123123",
				extraValidation: nil,
			},
			want:    false,
			wantErr: false,
		},
		{
			name: "Invalid_Prefix_Git_Test_1",
			args: args{
				uri: "https://github.com/zohaib194/CodebaseVisualizer3D.git123",
				extraValidation: func(url string) (isValid bool, err error) {
					return regexp.Match(`\.git$`, []byte(url))
				},
			},
			want:    false,
			wantErr: false,
		},
		{
			name: "Invalid_Prefix_Git_Test_2",
			args: args{
				uri:             "12312312https://github.com/zohaib194/CodebaseVisualizer3D",
				extraValidation: nil,
			},
			want:    false,
			wantErr: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, err := validateURI(tt.args.uri, tt.args.extraValidation)
			if (err != nil) != tt.wantErr {
				t.Errorf("validateURI() error = %v, wantErr %v", err, tt.wantErr)
				return
			}
			if got != tt.want {
				t.Errorf("validateURI() = %v, want %v", got, tt.want)
			}
		})
	}
}
