package org.apache.maven.surefire;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Collections.singletonMap;

public class Connector {
    private final BlockingDeque<AsynchronousSocketChannel> clients = new LinkedBlockingDeque<>();
    private final AsynchronousServerSocketChannel channel;

    public <T> Connector(SocketAddress serverAddress, int maxPendingConnections, Map<SocketOption<T>, T> options,
                         ExecutorService threadPool) throws IOException {
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(threadPool);
        channel = AsynchronousServerSocketChannel.open(group);
        for (Map.Entry<SocketOption<T>, T> option : options.entrySet()) {
            channel.setOption(option.getKey(), option.getValue());
        }
        channel.bind(serverAddress, maxPendingConnections);
    }

    protected AsynchronousServerSocketChannel getServerChannel() {
        return channel;
    }

    public BlockingDeque<AsynchronousSocketChannel> getClients() {
        return clients;
    }

    public void close() throws IOException {
        channel.close();
    }

    public AtomicBoolean waitForClients() {
        final AtomicBoolean success = new AtomicBoolean(true);
        getServerChannel()
                .accept(getServerChannel(), new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {
                    @Override
                    public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {
                        clients.add(result);
                        attachment.accept(attachment, this);
                        System.out.println("completed");
                        ByteBuffer readBuffer = ByteBuffer.allocate(32);
                        result.read(readBuffer, success, new WhoAmI());
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
                        success.set(false);
                        System.out.println("failed");
                    }
                });
        return success;
    }

    private static final class WhoAmI implements CompletionHandler<Integer, AtomicBoolean> {
        @Override
        public void completed(Integer result, AtomicBoolean success) {
            // here parse the who-am-i event received from the client and grab the channelID from JSON
        }

        @Override
        public void failed(Throwable exc, AtomicBoolean success) {
            success.set(false);
        }
    }

    public static void main(String[] args) throws Exception {
        ThreadFactory tf = new ThreadFactory() {
            private final ThreadFactory def = Executors.defaultThreadFactory();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = def.newThread(r);
                t.setDaemon(true);
                return t;
            }
        };
        final Connector connector = new Connector(new InetSocketAddress("localhost", 8080), 3,
                singletonMap(StandardSocketOptions.SO_REUSEADDR, true), Executors.newCachedThreadPool(tf));

        AtomicBoolean success = connector.waitForClients();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        AsynchronousSocketChannel channel = connector.getClients().takeFirst();
                        System.out.println(channel);
                        String body = "<html>\n" +
                                "<body>\n" +
                                "<h1>Hello, World!</h1>\n" +
                                "</body>\n" +
                                "</html>";
                        String msg = "HTTP/1.1 200 OK\n" +
                                "Date: Sun, 27 Oct 2019 12:28:53 GMT\n" +
                                "Server: Apache/2.2.14 (Win32)\n" +
                                "Last-Modified: Tue, 22 Oct 2019 19:15:56 GMT\n" +
                                "Content-Length: " + body.length() + "\n" +
                                "Content-Type: text/html\n" +
                                "Connection: Closed\n" +
                                "\n" +
                                body;
                        ByteBuffer bb = ByteBuffer.wrap(msg.getBytes(StandardCharsets.US_ASCII));
                        channel.write(bb);
                        //channel.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.setDaemon(true);
        t.start();
        Thread.sleep(30_000L);


        t.interrupt();
        System.out.println("success " + success.get());
    }
}
