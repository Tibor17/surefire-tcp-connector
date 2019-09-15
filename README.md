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
