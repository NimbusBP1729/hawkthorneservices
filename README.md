It's happening!
==========

The following project is a Java implementation of a sidescrolling game using [libgdx](http://libgdx.badlogicgames.com/)

Much of the inspiration for the development of this game came from the [Lua-developed single player Hawkthorne game](https://github.com/hawkthorne/hawkthorne-journey). However, this game aims to have a different scope of simple jumping puzzles and racing 2 or more players to a specific goal.

to run the server:
`mvn install -Pserver`

to run the client:
`mvn install -Pclient`

to package the application for android:
`mvn package -Pandroid`

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
