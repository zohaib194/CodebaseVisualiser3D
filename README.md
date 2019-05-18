# CodebaseVisualizer3D

CodebaseVisulizer3D is a bachelor project with the goal of making it easier to getting an overview of codebases. It consumes a git repo and represents the datastructure with 3D graphics and performs code complexity review on the code.


# Core team / Authors

- Kent Wincent Holt - 473209
- Zohaib Butt - 473219
- Eldar Hauge Torkelsen - 473180

# Dockerized install

- Navigate to repository root and run "sudo docker-compose up"

## Access application

- Open browser and go to "localhost".

# Manual install guide
- Clone the repository.

### Setup frontend

- Download and Install [Emscripten](http://kripken.github.io/emscripten-site/docs/getting_started/downloads.html)
- Navigate to the root folder of repository and run the following command.
  - ```git submodule update --init --recursive```
- Configuration: 
  - Connections: 
    - Navigate to the frontend/js/config folder and open the config.js file. 
    - Fill all fields with appropriate information:
      - "host_ip" is the clients ip address.
      - "host_port" is the clients port.
      - "api_ip" is api/back-end servers ip.
      - "api_port" should be 5016 as this is not configurable in the current version.
      - "api_servername" is api/back-end servers name (not really needed at this point).
  - Style: 
    - If you have a custom style you can edit it in frontend/js/config/style.js file. 
- For apache setup look up how to setup a virtualhost and make it point to "CodebaseVisualizer3D/frontend".

### Setup backend

#### Setup apiServer

- Install Golang
- Install MongoDB.
- Navigate to backend/apiServer folder from project root.

- Create a ".env" file where the following environment variables should exist:
  - "PORT" should be 5016.
  - "REPOSITORY_PATH" should be a folder to store cloned repositories. 
  - "DB_LOCATION" should be "/data/db" or the path to mongodb storage. 
  - "JAVA_PARSER" should be the absolute path to Java parser which relies at the following path from project root folder: CodebaseVisualizer3D/backend/parser/build/classes/java/main

#### Setup parser

- Install Java 11.0.2 or later.

- Download and install Gradle version 5.2 or later.

- Navigate to backend/parser folder from project root.
- Run "gradle release"

## Access the application

- Open a terminal an execute the following commands: 
  - "sudo service apache2 start"
  - "sudo mongod" 

- Navigate to "CodebaseVisualizer3D/backend/apiServer" folder and run "go build . && go run main.go"
- Open a browser and navigate to custom URL specified in the apache2 setup. 