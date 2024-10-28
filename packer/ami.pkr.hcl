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
  default = "ubuntu"
}

variable "profile" {
  type    = string
  default = "dev"
}

variable "aws_devuser" {
  type    = string
  default = "842675991249"
}

variable "database" {
  type    = string
  default = "cloud"
}

variable "user" {
  type    = string
  default = "raut"
}

variable "password" {
  type    = string
  default = "root"
}

variable "vpc_id" {
  type    = string
  default = "vpc-02a9166f3d342c7a9"
}

variable "subnet_id" {
  type    = string
  default = "subnet-046ddba93e36d2434"
}

variable "instance_type" {
  type    = string
  default = "t2.micro"
}

variable "aws_demouser" {
  type    = string
  default = "977099022616"
}


source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  profile         = "${var.profile}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225 2024"
  ami_regions     = ["${var.aws_region}"]
  ami_users       = ["${var.aws_devuser}", "${var.aws_demouser}"]

  aws_polling {
    delay_seconds = 120
    max_attempts  = 40
  }

  associate_public_ip_address = true
  vpc_id                      = "${var.vpc_id}"
  subnet_id                   = "${var.subnet_id}"
  instance_type               = "${var.instance_type}"
  source_ami                  = "${var.source_ami}"
  ssh_username                = "${var.ssh_username}"



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
    source      = "/home/runner/work/webapp/webapp/target/cloudproject-0.0.1-SNAPSHOT.war"
    destination = "/tmp/"
  }


   provisioner "file" {
    source      = "cloudwatch.json"
    destination = "/tmp/cloudwatch.json"
  }


  # Copy the csye6225.service file
  provisioner "file" {
    source      = "csye6225.service"
    destination = "/tmp/csye6225.service"
  }


  provisioner "file" {
    source      = "setup.sh"      # setup.sh file
    destination = "/tmp/setup.sh" # Where to place the script on the instance
  }


  provisioner "shell" {
    environment_vars = [
      "NEW_PASSWORD=${var.password}",
      "DATABASE_NAME=${var.database}",
      "DB_USER=${var.user}"
    ]
    inline = [
      "chmod +x /tmp/setup.sh", # Make the script executable
      "/tmp/setup.sh"           # Execute the script
    ]
  }
}
