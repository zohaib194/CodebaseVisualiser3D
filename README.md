# CodebaseVisualizer3D

CodebaseVisulizer3D is a bachelor project with the goal of making it easier to getting an overview of codebases. It consumes a git repo and represents the datastructure with 3D graphics and performs code complexity review on the code.


# Core team / Authors

- Kent Wincent Holt - 473209
- Zohaib Butt - 473219
- Eldar Hauge Torkelsen - 473180

# Install guide
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
      - "api_port" is api/back-end servers port.
      - "api_servername" is api/back-end servers name (not really needed at this point).
  - Style: 
    - If you have a custom style you can edit it in frontend/js/config/style.js file. 

### Setup backend

# Examples of use

# Changelog