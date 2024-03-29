import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import static java.util.concurrent.TimeUnit.MINUTES;

public class NettyClient {
    int port;
    Channel channel;
    EventLoopGroup workGroup = new NioEventLoopGroup();

    public NettyClient(int port){
        this.port = port;
    }

    public ChannelFuture connectLoop() throws Exception
    {
        try{
            Bootstrap b = new Bootstrap();
            b.group(workGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception
                {
                    socketChannel.pipeline().addLast(new NettyHandler());
                }
            });
            ChannelFuture channelFuture = b.connect("127.0.0.1", this.port).sync();
            this.channel = channelFuture.channel();

            return channelFuture;
        }finally{
        }
    }
    public void shutdown() throws InterruptedException
    {
        workGroup.shutdownGracefully();
        workGroup.awaitTermination( 1, MINUTES );
    }
}
