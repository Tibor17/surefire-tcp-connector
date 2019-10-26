import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Client
{
    private static BufferedReader userInputReader;

    private static boolean processReadySet( Set readySet ) throws Exception
    {
        Iterator iterator = readySet.iterator();
        while ( iterator.hasNext() )
        {
            SelectionKey key = (SelectionKey) iterator.next();
            iterator.remove();

            if ( key.isConnectable() )
            {
                boolean connected = processConnect( key );
                if ( !connected )
                {
                    return true; // Exit
                }
            }

            if ( key.isReadable() )
            {
                String msg = processRead( key );
                System.out.println( "[Server]: " + msg );
            }

            if ( key.isWritable() )
            {
                System.out.print( "Please enter a message(Bye to quit):" );
                String msg = userInputReader.readLine();

                if ( msg.equalsIgnoreCase( "bye" ) )
                {
                    return true; // Exit
                }
                SocketChannel sChannel = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.wrap( msg.getBytes() );
                sChannel.write( buffer );
            }
        }
        return false; // Not done yet
    }

    private static boolean processConnect( SelectionKey key ) throws Exception
    {
        SocketChannel channel = (SocketChannel) key.channel();
        while ( channel.isConnectionPending() )
        {
            channel.finishConnect();
        }
        return true;
    }

    private static String processRead( SelectionKey key ) throws Exception
    {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate( 1024 );
        sChannel.read( buffer );
        buffer.flip();
        Charset charset = StandardCharsets.UTF_8;
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = decoder.decode( buffer );
        return charBuffer.toString();
    }

    public static void main( String[] args ) throws Exception
    {
        InetAddress serverIPAddress = InetAddress.getByName( "localhost" );
        int port = 19000;
        InetSocketAddress serverAddress = new InetSocketAddress( serverIPAddress, port );
        Selector selector = Selector.open();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking( false );
        channel.connect( serverAddress );
        int operations = SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE;
        channel.register( selector, operations );

        userInputReader = new BufferedReader( new InputStreamReader( System.in ) );
        while ( selector.select() > 0 )
        {
            boolean doneStatus = processReadySet( selector.selectedKeys() );
            if ( doneStatus )
            {
                break;
            }
        }
        channel.close();
    }
}
