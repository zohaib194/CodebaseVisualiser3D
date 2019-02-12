FROM golang:latest

# Add server files to correct gopath location
COPY . /go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/
WORKDIR /go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer

# setting up api documentation
RUN apt-get update
RUN apt-get -y install apache2 
RUN curl -sL https://deb.nodesource.com/setup_11.x | bash -
RUN apt-get install -y nodejs
RUN npm install
RUN node_modules/.bin/apidoc -i ./apiServer/ -o /var/www/html/ -c ./ -e .*vendor -e ./apiServer/node_modules/
RUN service apache2 start

# Get all go dependencies
RUN go get ./...

RUN go build .

CMD go run .