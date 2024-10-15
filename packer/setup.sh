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
sudo systemctl start mysqld
sudo systemctl enable mysqld

# Set root password for MySQL and secure installation
echo "Setting MySQL root password and securing installation"
sudo mysql <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
EOF

# Login to MySQL as root and create user and database
echo 'Setting up MySQL user and database'
NEW_PASSWORD='root'  # Change this to your desired password
DATABASE_NAME='cloud'  # Change this to your desired database name
DB_USER='raut'         # Change this to your desired database username

# Login as root and create user and database
sudo mysql -u root -proot <<EOF
CREATE DATABASE $DATABASE_NAME;
CREATE USER '$DB_USER'@'localhost' IDENTIFIED BY '$NEW_PASSWORD';
GRANT ALL PRIVILEGES ON $DATABASE_NAME.* TO '$DB_USER'@'localhost';
FLUSH PRIVILEGES;
EXIT;
EOF

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

# Clean up
echo 'Cleaning up...'
sudo apt-get clean