

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncNIOServer {

    public static void main(String[] args) {

        try (final AsynchronousServerSocketChannel listener =
                     AsynchronousServerSocketChannel.open()) {

            listener.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            listener.bind(new InetSocketAddress("localhost", 8080));

            while (true) {

                // callback 1
                listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                    @Override
                    public void completed(AsynchronousSocketChannel connection, Void v) {
                        listener.accept(null, this); // get ready for next connection
                        final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

                        // callback 2
                        connection.read(buffer, connection, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                            @Override
                            public void completed(Integer result, final AsynchronousSocketChannel scAttachment) {
                                if (result == -1) {
                                    try {
                                        scAttachment.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                // callback 3
                                scAttachment.write((ByteBuffer) buffer.flip(), buffer, new CompletionHandler<Integer, ByteBuffer>() {
                                    @Override
                                    public void completed(Integer result, ByteBuffer bbAttachment) {
                                        if (bbAttachment.hasRemaining()) {
                                            scAttachment.write(bbAttachment, bbAttachment, this);
                                        } else {
                                            bbAttachment.clear();
                                        }
                                    }
                                    @Override
                                    public void failed(Throwable t, ByteBuffer bbAttachment) {
                                        t.printStackTrace();
                                    }
                                });

                            }
                            @Override
                            public void failed(Throwable t, AsynchronousSocketChannel scAttachment) {
                                t.printStackTrace();
                            }
                        });

                    }
                    @Override
                    public void failed(Throwable t, Void v) {
                        t.printStackTrace();
                    }
                });

                System.in.read(); // so we don't exit before a connection is established

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}