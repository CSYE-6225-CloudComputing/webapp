#!/bin/bash

sudo cp /opt/cloudproject/csye6225.service /etc/systemd/system/

# Reload systemd to pick up the changes
sudo systemctl daemon-reload

sudo systemctl start csye6225.service


sudo systemctl enable csye6225.service



sudo systemctl status csye6225.service