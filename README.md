It's happening!
==========

The following project is a Java implementation of a sidescrolling game using [libgdx](http://libgdx.badlogicgames.com/)

Much of the inspiration for the development of this game came from the [Lua-developed single player Hawkthorne game](https://github.com/hawkthorne/hawkthorne-journey). However, this game aims to have a different scope of simple jumping puzzles and racing to a specific goal.

#to run the server:
#`mvn install -Pserver`

#to run the client:
#`mvn install -Pclient`

to package the application for android:
`mvn package -Pandroid`


Installation instructions (Linux)
#################################
install git
> sudo yum install git

#install maven
> cd /usr/local/
> wget http://apache.petsads.us/maven/maven-3/3.1.1/binaries/apache-maven-3.1.1-bin.tar.gz
> tar xvf apache-maven-3.1.1-bin.tar.gz

#add the following to your ~/.bashrc
> echo 'export M2_HOME=/usr/local/apache-maven-3.1.1
> export M2=$M2_HOME/bin
> export PATH=$M2:$PATH' >> ~/.bashrc

#install tools.jar(as well as the rest of java) if it's not in your 'jdk'
> sudo yum install java-1.6.0-openjdk-devel.x86_64

Screenshot From Android:
=========================
![ScreenShot](https://dl.dropboxusercontent.com/u/13978314/hawkthorne/clientServerSnapshots/resizedAndroid.png)

if you want to use a port or address other than the defaults set the following environment variables:
`HAWK_PORT`(default=12345)

`HAWK_ADDRESS`(default=localhost)


Screenshot from Desktop:
========================
![ScreenShot](https://dl.dropboxusercontent.com/u/13978314/hawkthorne/clientServerSnapshots/multi.png)


Snapshots
=========
[APK snapshot](https://dl.dropboxusercontent.com/u/13978314/hawkthorne/clientServerSnapshots/hawkthorneservices-android.apk)

[client snapshot](https://dl.dropboxusercontent.com/u/13978314/hawkthorne/clientServerSnapshots/hawkthorneservices-client-1.0.0-SNAPSHOT-jar-with-dependencies.jar)

[server snapshot](https://dl.dropboxusercontent.com/u/13978314/hawkthorne/clientServerSnapshots/hawkthorneservices-server-1.0.0-SNAPSHOT-jar-with-dependencies.jar)
