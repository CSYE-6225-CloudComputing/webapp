#!/bin/bash

echo 'Updating Packages'
sudo apt-get update -y
sleep 10

echo 'Installing OpenJDK 21'
sudo apt-get install -y openjdk-21-jdk
echo 'Installed Java successfully.'
java -version

echo 'Installing Maven'
sudo apt-get install -y maven
echo 'Installed Maven successfully.'
mvn -version

echo 'Installing MySQL Server'
sudo apt-get install -y mysql-server
echo 'Installed MySQL Server successfully.'

# Start and enable MySQL service
sudo systemctl start mysql
sudo systemctl enable mysql

# Set root password for MySQL
echo "Setting MySQL root password"
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';"


# Access environment variables correctly
NEW_PASSWORD=$NEW_PASSWORD   # No single quotes, just the variable name
DATABASE_NAME=$DATABASE_NAME  # Same here
DB_USER=$DB_USER              # And here

# Display the variables
echo "Database Name: '$DATABASE_NAME'"
echo "Database User: $DB_USER"
echo "Database Password: '$NEW_PASSWORD'"


# Create user and database
sudo mysql -u root -proot -e "
DROP USER IF EXISTS '$DB_USER'@'localhost';
DROP DATABASE IF EXISTS $DATABASE_NAME;
CREATE DATABASE $DATABASE_NAME;
CREATE USER '$DB_USER'@'localhost' IDENTIFIED BY '$NEW_PASSWORD';
GRANT ALL PRIVILEGES ON $DATABASE_NAME.* TO '$DB_USER'@'localhost';
FLUSH PRIVILEGES;
"

echo 'MySQL user and database setup completed successfully.'

# Create group csye6225
echo 'Creating group csye6225'
sudo groupadd csye6225

# Create no-login user csye6225 and add to csye6225 group
echo 'Creating user csye6225 with no-login shell and adding to group csye6225'
sudo useradd -g csye6225 --shell /usr/sbin/nologin csye6225

# Change ownership of the necessary directories/files
echo 'Changing ownership of application files to csye6225 user and group'
sudo mkdir -p /opt/cloudproject
sudo mv /tmp/cloudproject-0.0.1-SNAPSHOT.war /opt/cloudproject
sudo chown -R csye6225:csye6225 /opt/cloudproject
sudo chmod 755 /opt/cloudproject/cloudproject-0.0.1-SNAPSHOT.war

# Define your database URL and credentials
DB_URL="jdbc:mysql://localhost:3306/cloud"  
DB_USERNAME="raut"                           
DB_PASSWORD="root"                           


# Create .env file with DB properties
echo "DB_URL='$DB_URL'" | sudo tee /opt/cloudproject/.env > /dev/null
echo "DB_USERNAME='$DB_USERNAME'" | sudo tee -a /opt/cloudproject/.env > /dev/null
echo "DB_PASSWORD='$DB_PASSWORD'" | sudo tee -a /opt/cloudproject/.env > /dev/null

# Change ownership and restrict access to the .env file
sudo chown csye6225:csye6225 /opt/cloudproject/.env
sudo chmod 755 /opt/cloudproject/.env  # Secure the file by restricting access

echo '.env file created and moved to /opt/cloudproject/.env'



# Copy csye6225.service file and configure systemd
echo 'Setting up csye6225 service'
sudo cp /tmp/csye6225.service /etc/systemd/system/

# Reload systemd to pick up the changes
sudo systemctl daemon-reload

# Start and enable csye6225 service
sudo systemctl start csye6225.service
sudo systemctl enable csye6225.service

# Check the status of the service
echo 'Checking status of csye6225 service...'
sudo systemctl status csye6225.service

# Display journal logs for csye6225 service
sudo journalctl -xeu csye6225.service

# Clean up
echo 'Cleaning up...'
sudo apt-get clean
