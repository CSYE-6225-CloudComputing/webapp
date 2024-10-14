packer {
  required_plugins {
    amazon = {
      version = ">= 1.2.8"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-0866a3c8686eaeeba"
}

variable "ssh_username" {
  type    = string
  default = "ubuntu" # Default SSH username for Ubuntu AMIs
}

variable "profile" {
  type    = string
  default = "dev" # Your AWS CLI profile name
}

variable "aws_devuser" {
  type    = string
  default = "842675991249" # Your AWS account ID for sharing AMIs
}


source "amazon-ebs" "my-ami" {
  region          = var.aws_region
  profile         = var.profile
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  ami_regions     = [var.aws_region]
  ami_users       = [var.aws_devuser]

  aws_polling {
    delay_seconds = 120
    max_attempts  = 50
  }

  instance_type = "t2.micro"
  source_ami    = var.source_ami
  ssh_username  = var.ssh_username

  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 50
    volume_type           = "gp2"
  }
}

build {
  sources = [
    "source.amazon-ebs.my-ami"
  ]

  provisioner "file" {
    source      = "target/cloudproject-0.0.1-SNAPSHOT.war"
    destination = "/tmp/"
  }

  provisioner "file" {
    source      = "setup.sh"      # Path to your setup.sh file
    destination = "/tmp/setup.sh" # Where to place the script on the instance
  }

  provisioner "shell" {
    inline = [
      "chmod +x /tmp/setup.sh", # Make the script executable
      "/tmp/setup.sh"           # Execute the script
    ]
  }
}