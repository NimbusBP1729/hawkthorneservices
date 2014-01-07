The following project is a java implementation of a sidescrolling game using libgdx

to run the server:
mvn install -Pserver

to run the client:
mvn install -Pclient

if you want to use a port or address other than the defaults set the following environment variables:
HAWK_PORT(default=12345)
HAWK_ADDRESS(default=localhost)
