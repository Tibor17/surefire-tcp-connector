import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

class Responder
{

    String serverSentence;
    BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );

    // on client process termination or
    // client sends EXIT then to return false to close connection
    // else return true to keep connection alive
    // and continue conversation
    synchronized public boolean responderMethod( Socket connectionSocket )
    {
        try
        {

            BufferedReader inFromClient = new BufferedReader(
                    new InputStreamReader( connectionSocket.getInputStream() ) );

            DataOutputStream outToClient = new DataOutputStream( connectionSocket.getOutputStream() );

            String clientSentence = inFromClient.readLine();

            // if client process terminates it get null, so close connection
            if ( clientSentence == null || clientSentence.equals( "EXIT" ) )
            {
                return false;
            }

            if ( clientSentence != null )
            {
                System.out.println( "client : " + clientSentence );
            }
            serverSentence = br.readLine() + "\n";

            outToClient.writeBytes( serverSentence );

            return true;

        }
        catch ( SocketException e )
        {
            System.out.println( "Disconnected" );
            return false;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return false;
        }
    }
}