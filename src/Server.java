import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
}
