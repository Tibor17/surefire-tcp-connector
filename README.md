Motivated by:

- [netty](https://github.com/deepanprabhu/netty-twoway-tcp-client-server)
- [java-api](https://javadeveloperszone.wordpress.com/2013/04/20/java-tcp-chat-multiple-client/) and [example](https://cs.lmu.edu/~ray/notes/javanetexamples/)

The commandline of `netty` project:

```
t2 = 1825 millis
t3 = 0 millis
t4 = 52 millis
t5 = 43 millis
Received Message : Hello

Received Message : Hello There

Received Message : Thanks For Reply !!
```

The plugin `maven-shade-plugin` builds and shrinks the JAR file. The size of the JAR file is 1 MB.
The original size of `netty-all` is 4 MB.

The pure TCP/IP server starts up within `5 milli seconds`.
This is the console output:
```
SO_KEEPALIVE=false
SO_TIMEOUT=0
TCP_NODELAY=false
SO_OOBINLINE=false
SO_LINGER=-1
```

### References:
https://programmer.group/java-network-io-programming-bio-nio-aio.html
https://gist.github.com/nyg/dc1cbdc4e262b549fc3c01a7f509d42b
https://gist.githubusercontent.com/trustin/3081315/raw/3e90da8a1574bbe8c73eb3fca0eeaeac0da9463e/EchoTest.java
http://www.java2s.com/Tutorials/Java/Java_Network/0080__Java_Network_Asynchronous_Socket_Channels.htm
https://programming.vip/docs/java-non-blocking-io-and-asynchronous-io.html
http://www.java2s.com/Tutorials/Java/Java_Network/0070__Java_Network_Non-Blocking_Socket.htm

http://www.java2s.com/Tutorials/Java/Java_Network/0070__Java_Network_Non-Blocking_Socket.htm
https://crunchify.com/java-nio-non-blocking-io-with-server-client-example-java-nio-bytebuffer-and-channels-selector-java-nio-vs-io/
https://www.baeldung.com/java-nio2-async-socket-channel
https://gist.github.com/ochinchina/72cc23220dc8a933fc46
https://blog.overops.com/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-json/
https://javaee.github.io/jsonp/
https://mvnrepository.com/artifact/org.glassfish/javax.json/1.1.4
https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core/2.9.9
https://www.baeldung.com/jackson-streaming-api
