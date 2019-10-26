import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static java.nio.channels.SelectionKey.*;

public class Server
{
    public static void main( String[] args ) throws Exception
    {
        InetAddress hostIPAddress = InetAddress.getByName( "localhost" );
        int port = 19_000;
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.configureBlocking( false );
        ssChannel.socket().bind( new InetSocketAddress( hostIPAddress, port ) );
        Selector selector = Selector.open();
        ssChannel.register( selector, OP_ACCEPT );
        while ( selector.select() > 0 )
        {
            processReadySet( selector.selectedKeys() );
        }
    }

    private static void processReadySet( Set readySet ) throws Exception
    {
        ByteBuffer buffer = ByteBuffer.allocateDirect( 64 * 1024 );
        Iterator iterator = readySet.iterator();
        while ( iterator.hasNext() )
        {
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();

            if ( !key.isValid() )
            {
                continue;
            }

            if ( key.isAcceptable() )
            {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel channel = serverSocketChannel.accept();
                channel.configureBlocking( false );
                channel.register( key.selector(), OP_READ | OP_WRITE );
            }

            if ( key.isReadable() )
            {
                String msg = processRead( key );
                if ( msg.length() > 0 )
                {
                    SocketChannel channel = (SocketChannel) key.channel();
                }
            }

            if ( key.isWritable() )
            {
                SocketChannel channel = (SocketChannel) key.channel();
                buffer.clear();
                channel.write( buffer );
            }
        }
    }

    private static String processRead( SelectionKey key ) throws Exception
    {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate( 1024 );
        int bytesCount = sChannel.read( buffer );
        if ( bytesCount > 0 )
        {
            buffer.flip();
            return new String( buffer.array() );
        }
        return "NoMessage";
    }
}
