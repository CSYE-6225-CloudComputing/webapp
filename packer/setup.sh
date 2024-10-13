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
sudo mysql -u root -p root <<EOF
CREATE DATABASE $DATABASE_NAME;
CREATE USER '$DB_USER'@'localhost' IDENTIFIED BY '$NEW_PASSWORD';
GRANT ALL PRIVILEGES ON $DATABASE_NAME.* TO '$DB_USER'@'localhost';
FLUSH PRIVILEGES;
EXIT;
EOF

echo 'MySQL user and database setup completed successfully.'

sudo systemctl start mysqld
sudo systemctl enable mysqld

echo 'mysql is running'

# Clean up
echo 'Cleaning up...'
sudo apt-get clean