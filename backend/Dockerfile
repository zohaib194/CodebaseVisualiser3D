FROM golang:latest

# Add server files to correct gopath location
COPY . /go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/
WORKDIR /go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer

# Get all go dependencies
RUN go get . ./controller/ ./model/

RUN go build .

CMD go run .