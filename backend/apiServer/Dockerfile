# Based of image with apache2
FROM httpd:2.4

COPY . /go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer
WORKDIR /go/src/github.com/zohaib194/CodebaseVisualizer3D/backend/apiServer

# Install npm with requirements
RUN apt-get update && apt-get -y upgrade
RUN apt-get -y install gnupg2
RUN apt-get -y install curl
RUN curl -sL https://deb.nodesource.com/setup_11.x | bash -
RUN apt-get -y install nodejs
RUN npm install


# Generating documentation into htdocs.
RUN ./node_modules/.bin/apidoc -i ./ -o /usr/local/apache2/htdocs/ -c ./ -e .*vendor -e ./node_modules/