# Secure-Communication
SEC project starting point


# Deployment & Testing

To deploy the project, the host computer requires to have Apache Maven, Docker, Docker Compose and PostgreSQL installed.
The following instructions are meant for linux operative systems (tested on Ubuntu 20.04):

Configuring the database:
- The database must operate locally on port 5432 with the user name 'postgres' and password 'root'. (This must be done in such a way that the
connection url is 'jdbc:postgresql://localhost:5432/sec').
- Create a database with the name 'sec': "sudo -u postgres createdb sec"

(This guide can be followed for the configuration https://www.digitalocean.com/community/tutorials/how-to-install-postgresql-on-ubuntu-20-04-quickstart-pt)

Configuring the clients and servers:
- Start a new terminal, as this current one will be used by docker or edit the "deploy.sh" script to send to the background the containers logging.
- Navigate to the "Deploy" folder.
- Give the execution permission to the "deploy.sh" script using the command "chmod +x deploy.sh".
- Execute the "deploy.sh" script with sudo permissions "sudo ./deploy.sh".


After executing the command there will be an initial delay of around X seconds before the clients start automatically sending location reports to the server. This delay is
used to allow all the applications to initialize.
