pipeline {
    agent none
    stages {
        stage('Build') {
            agent { label 'operations' }
            steps {
                echo "OPERATIONS BUILD STAGE!"
                sudo docker build -t codeviz3d:0.1 --build-arg website_files=./workshop/operations/frontend ./workshop/operations/frontend/Dockerfile
                sudo docker run -p 8080:80 --name codeviz3d codeviz3d:0.1
            }
        }
        stage('Test') {
            agent { label 'operations' }
            steps {
                echo "OPERATIONS TEST STAGE!"
            }
        }
        stage('Deploy') {
            agent { label 'operations' }
            steps {
                echo "OPERATIONS DEPLOY STAGE!"
            }
        }

        stage('Build') {
            agent { label 'development' }
            steps {
                echo "DEVELOPMENT BUILD STAGE!"
                sudo docker build -t codeviz3d:0.1 --build-arg website_files=./workshop/development/frontend ./workshop/development/frontend/Dockerfile
                sudo docker run -p 8080:80 --name codeviz3d codeviz3d:0.1
            }
        }
        stage('Test') {
            agent { label 'development' }
            steps {
                echo "DEVELOPMENT TEST STAGE!"
            }
        }
        stage('Deploy') {
            agent { label 'development' }
            steps {
                echo "DEVELOPMENT DEPLOY STAGE!"
            }
        }
    }
}