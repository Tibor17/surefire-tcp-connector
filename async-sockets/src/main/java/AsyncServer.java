import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.nio.channels.AsynchronousChannelGroup.withFixedThreadPool;
import static java.nio.channels.AsynchronousServerSocketChannel.open;
import static java.util.concurrent.Executors.defaultThreadFactory;

public class AsyncServer
{
    static class ChannelHandler implements CompletionHandler<Integer, Attachment>
    {
        @Override
        public void completed( Integer result, Attachment attachment )
        {
            System.out.println( "completed" );
            attachment.setReadMode(true);
            attachment.getBuffer().clear();
            attachment.getClient().read(attachment.getBuffer(), attachment, this);
        }

        @Override
        public void failed( Throwable exc, Attachment attachment )
        {
            System.out.println( "failed" );
        }
    }

    public static void main( String[] args ) throws Exception
    {
        AsynchronousChannelGroup group = withFixedThreadPool( 10, defaultThreadFactory() );

        long startTime = System.currentTimeMillis();

        final AsynchronousServerSocketChannel server = open( group ).bind( new InetSocketAddress(19000) );

        InetSocketAddress localAddress = (InetSocketAddress) server.getLocalAddress();
        System.out.println( "server bound to local port " + localAddress.getPort() );

        long endTime = System.currentTimeMillis();

        System.out.println("server started within " + (endTime - startTime) + " millis");

        Attachment att = new Attachment();
        att.setServer(server);
        server.accept(att, new CompletionHandler<AsynchronousSocketChannel, Attachment>() {

            @Override
            public void completed(AsynchronousSocketChannel client, Attachment att) {
                try {
                    SocketAddress clientAddr = client.getRemoteAddress();
                    System.out.println("Receive a new connection:" + clientAddr);

                    // After receiving a new connection, the server should call the accept method again and wait for the new connection to come in.
                    att.getServer().accept(att, this);

                    Attachment newAtt = new Attachment();
                    newAtt.setServer(server);
                    newAtt.setClient(client);
                    newAtt.setReadMode(true);
                    newAtt.setBuffer(ByteBuffer.allocate(2048));

                    // Anonymous implementation classes can also continue to be used here, but the code is ugly, so a class is specifically defined here.
                    client.read(newAtt.getBuffer(), newAtt, new ChannelHandler());
                    newAtt.getBuffer().flip();
                    System.out.println( new String( newAtt.getBuffer().array(), 0, newAtt.getBuffer().limit() ) );
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable t, Attachment att) {
                System.out.println("accept failed");
            }
        });
        System.out.println("finished");
        // To prevent main threads from exiting
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
        }
        System.out.println("exit");
    }

    public static class Attachment
    {
        private volatile AsynchronousServerSocketChannel server;
        private volatile AsynchronousSocketChannel client;
        private volatile boolean isReadMode;
        private volatile ByteBuffer buffer;

        public AsynchronousServerSocketChannel getServer()
        {
            return server;
        }

        public void setServer( AsynchronousServerSocketChannel server )
        {
            this.server = server;
        }

        public AsynchronousSocketChannel getClient()
        {
            return client;
        }

        public void setClient( AsynchronousSocketChannel client )
        {
            this.client = client;
        }

        public boolean isReadMode()
        {
            return isReadMode;
        }

        public void setReadMode( boolean readMode )
        {
            isReadMode = readMode;
        }

        public ByteBuffer getBuffer()
        {
            return buffer;
        }

        public void setBuffer( ByteBuffer buffer )
        {
            this.buffer = buffer;
        }
    }

    /*void run() throws Exception
    {
        AsynchronousServerSocketChannel server = open();
        server.bind(null);
        Future<AsynchronousSocketChannel> acceptFuture = server.accept();
        AsynchronousSocketChannel worker = acceptFuture.get();
        if (worker != null && worker.isOpen()) {
            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(32);
                Future<Integer> readResult  = worker.read(buffer);

                // perform other computations

                readResult.get();

                buffer.flip();
                Future<Integer> writeResult = worker.write(buffer);

                // perform other computations

                writeResult.get();
                buffer.clear();
            }
            worker.close();
            server.close();
        }
    }*/
}
