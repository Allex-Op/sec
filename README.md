# Secure-Communication
SEC project starting point


# Deployment & Testing

To deploy the project, the host computer requires to have Apache Maven, Docker and Docker Compose installed.
Also the following instructions are meant for linux operative systems (tested on Ubuntu 20.04):
- Start a new terminal, as this current one will be used by docker or edit the "deploy.sh" script to send to the background the containers logging.
- Navigate to the "Deploy" folder.
- Give the execution permission to the "deploy.sh" script using the command "chmod +x deploy.sh".
- Execute the "deploy.sh" script with sudo permissions "sudo ./deploy.sh".
- Wait for the compilation of the projects and docker images & containers to complete.

After executing the steps above check if the containers are running with the command "sudo docker ps".

It's also possible to check the logs of a container in specific with the command "sudo docker -f logs [id]".