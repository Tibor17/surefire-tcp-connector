import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;

public class Main {
    public static void main( String[] args) throws Exception
    {
        // ServerFlow.OpenServerPort(12000).channel().closeFuture().sync();
        long t1 = System.currentTimeMillis();
        NettyServer nettyServer = new NettyServer(12000);
        nettyServer.connectLoop();
        long t2 = System.currentTimeMillis();
        System.out.println( "t2 = " + (t2 - t1) + " millis" );
        nettyServer.getChannelFuture().sync();
        long t3 = System.currentTimeMillis();
        System.out.println( "t3 = " + (t3 - t2) + " millis" );

        NettyClient nettyClient = new NettyClient(12000);
        ChannelFuture channelFuture = nettyClient.connectLoop();
        long t4 = System.currentTimeMillis();
        System.out.println( "t4 = " + (t4 - t3) + " millis" );
        if (channelFuture.isSuccess()) {
            channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer("Hello\r\n".getBytes()));
        }
        long t5 = System.currentTimeMillis();
        System.out.println( "t5 = " + (t5 - t4) + " millis" );
        Thread.sleep(1000L);
        channelFuture.await();
        nettyServer.shutdown();
    }
}
