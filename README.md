# Cloud Guard
## Introduction
Cloud Guard is one of CSE481 S Security Capstone projects developed by Shibin(Jack) Xu (shibix@cs.washington.edu), Zhaoyuan(Matt) Xu (xzy1998@cs.washington.edu), and Xinghan(Jeff) Zhao (zhaox27@cs.washington.edu) at University of Washington.

## Deploy your server
Obtain an [AWS EC2 ubuntu server](https://aws.amazon.com/ec2/) <br />
Set all traffic on port 7729 <br />
Allow http(s) traffic on your server <br />
SSH into your server <br />
`git clone git@github.com:shibix/cloudguard.git` <br />
`cd cloudguard` <br />
Enter `tmux` <br />
`./DEPLOY` <br />
Save URL (e.g. ec2-***-***.compute.amazonaws.com/CloudGuard.zip) <br />
Exit `tmux` <br />

## Download client app on Unix-like systems (e.g. Linux, Mac OS)
Visit the URL saved <br />
`unzip CloudGuard.zip` <br />
`cd CloudGuard` <br />
`./client_linux` <br />
