import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPMultiClient {

    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;

        BufferedReader inFromUser =
                new BufferedReader(
                        new InputStreamReader(System.in));

        Socket clientSocket = new Socket("127.0.0.1", 6789);

        /*
         * see the description of the option in {@link SocketOptions}.
         */
        System.out.println( "SO_KEEPALIVE=" + clientSocket.getKeepAlive() );
        System.out.println( "SO_TIMEOUT=" + clientSocket.getSoTimeout() );
        System.out.println( "TCP_NODELAY=" + clientSocket.getTcpNoDelay() );
        System.out.println( "SO_OOBINLINE=" + clientSocket.getOOBInline() );
        System.out.println( "SO_LINGER=" + clientSocket.getSoLinger() );

        while (true) {
            DataOutputStream outToServer =
                    new DataOutputStream(
                            clientSocket.getOutputStream());

            BufferedReader inFromServer =
                    new BufferedReader(
                            new InputStreamReader(
                                    clientSocket.getInputStream()));

            sentence = inFromUser.readLine();

            outToServer.writeBytes(sentence + '\n');

            if (sentence.equals("EXIT")) {
                break;
            }

            modifiedSentence = inFromServer.readLine();

            System.out.println("FROM SERVER: " + modifiedSentence);

        }
        clientSocket.close();
    }
}