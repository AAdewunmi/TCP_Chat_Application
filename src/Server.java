import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Adrian Adewunmi
 * {@code @date} 25 Sept 2022
 * @description Server Class
 */


public class Server {

    private static int uniqueId;
    private ArrayList<ClientThread> al;
    private SimpleDateFormat sdf;
    private int port;
    private boolean keepGoing;
    private final String notif = " *** ";

    public Server(int port) {
        this.al = new ArrayList<ClientThread>();
        this.sdf = new SimpleDateFormat("HH:mm:ss");
        this.port = port;
    }

    // Implement start() method for starting server process
    public void start(){
        keepGoing = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(keepGoing){
                display("Server waiting for Clients on port " + port + ".");
                Socket socket = serverSocket.accept();
                if (!keepGoing) {
                    break;
                    ClientThread thread =
                }
            }
        }
    }

    private void display(String message) {
        String time = sdf.format(new Date()) + " " + message;
        System.out.println(time);
    }
}
