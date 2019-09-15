import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NettyServer {
    private final int port;

    private final EventLoopGroup workerGroup;

    private volatile ChannelFuture channelFuture;

    public NettyServer(int port) {
        this.port = port;
        final ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ThreadFactory daemonThreadFactory = new ThreadFactory()
        {
            public Thread newThread( Runnable r )
            {
                Thread t = threadFactory.newThread( r );
                t.setDaemon( true );
                return t;
            }
        };
        ExecutorService executor = Executors.newCachedThreadPool(daemonThreadFactory);
        workerGroup = new NioEventLoopGroup(3, executor);
    }

    public void connectLoop() throws Exception
    {
        ServerBootstrap b = new ServerBootstrap(); // (2)
        b.group(workerGroup)
                .channel(NioServerSocketChannel.class) // (3)
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception
                    {
                        ch.pipeline().addLast(new NettyHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind(port).sync(); // (7)


        f.channel();
        this.channelFuture = f;
        //f.channel().closeFuture().sync();
    }

    public void shutdown() {
        workerGroup.shutdownGracefully();
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }
}
