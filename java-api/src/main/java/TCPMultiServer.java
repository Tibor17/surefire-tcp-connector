import java.net.ServerSocket;
import java.net.Socket;

public class TCPMultiServer
{
    public static void main( String argv[] ) throws Exception
    {
        long t1 = System.currentTimeMillis();
        ServerSocket welcomeSocket = new ServerSocket( 6789 );
        long t2 = System.currentTimeMillis();
        System.out.println( "t2 = " + (t2 - t1) + " millis" );

        Responder h = new Responder();
        // server runs for infinite time and
        // wait for clients to connect
        while ( true )
        {
            // waiting..
            Socket connectionSocket = welcomeSocket.accept();

            // on connection establishment start a new thread for each client
            // each thread shares a common responder object
            // which will be used to respond every client request
            // need to synchronize method of common object not to have unexpected behaviour
            Thread t = new Thread( new MyServer( h, connectionSocket ) );

            // start thread
            t.start();
        }
    }
}
