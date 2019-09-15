import java.io.IOException;
import java.net.Socket;

class MyServer implements Runnable {

    Responder h;
    Socket connectionSocket;

    public MyServer(Responder h, Socket connectionSocket) {
        this.h = h;
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {

        while (h.responderMethod(connectionSocket)) {
            try {
                // once an conversation with one client done,
                // give chance to other threads
                // so make this thread sleep
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        try {
            connectionSocket.close();
        } catch ( IOException ex) {
        }

    }

}