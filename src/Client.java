import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Adrian Adewunmi
 * @date 25 Sept 2022
 * @description Client Class
 */
public class Client {

    private String notif = " *** ";
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;
    private String server, username;
    private int port;

    public Client(String server, String username, int port) {
        this.server = server;
        this.username = username;
        this.port = port;
    }
}
