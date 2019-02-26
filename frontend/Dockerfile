# Based of httpd:24 image.
FROM httpd:2.4

# Update from repository commented while testing.
RUN apt-get update && apt-get install -y

# Add argument specifying where the webpage files exists outside container.
# Usage/Assignment: --build-arg website_files=/host/file/location.
ARG website_files

# Copy files form host to apache2 htdocs.
COPY ${website_files} /usr/local/apache2/htdocs/