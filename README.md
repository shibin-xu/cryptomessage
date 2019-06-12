# Crypto Message
## Introduction
Cloud Guard is one of CSEP 590 Applied Cryptography projects developed by Shibin(Jack) Xu (shibix@cs.washington.edu) and Sheridan Thirsk (threeup@gmail.com) at University of Washington.

## Deploy your server
Obtain an [AWS EC2 ubuntu server](https://aws.amazon.com/ec2/) <br />
Set all traffic on port 7729 <br />
Allow http(s) traffic on your server <br />
SSH into your server <br />
`git@github.com:shibix/cryptomessage.git` <br />
`cd cryptomessage ` <br />
Enter `tmux` <br />
`./DEPLOY` <br />
Save URL (e.g. ec2-***-***.compute.amazonaws.com/CloudGuard.zip) <br />
Exit `tmux` <br />

## Getting Started

install zmq 4.0.4
* Linux: `sudo apt-get install libzmq3-dev`
* OSX: `brew install zmq`
* Windows: `http://zeromq.org/distro:microsoft-windows`

install dependencies:
`npm install`

change zeromq build target
`npm rebuild zeromq --runtime=electron --target=2.0.16`

run electron 
`npm start`
