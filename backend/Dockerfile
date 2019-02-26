FROM golang:latest

# Add server files to correct gopath location
COPY . /go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/


# Java
RUN apt-get update
RUN apt-get install openjdk-8-jdk openjdk-8-jre -y

# unzip
RUN apt-get install unzip

# Gradle
RUN wget --no-check-certificate https://services.gradle.org/distributions/gradle-5.2.1-all.zip
RUN mkdir /opt/gradle 
RUN unzip -d /opt/gradle gradle-5.2.1-all.zip

# Antlr4
WORKDIR /usr/local/lib
RUN wget --no-check-certificate https://www.antlr.org/download/antlr-4.7.2-complete.jar

# Build the java parser
WORKDIR /go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/parser
RUN /opt/gradle/gradle-5.2.1/bin/gradle fatjar

# Get all go dependencies
WORKDIR /go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer
RUN go get . ./controller/ ./model/

RUN go build .
CMD go run .
