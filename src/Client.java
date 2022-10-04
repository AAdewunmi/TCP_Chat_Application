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
    private String server, userName;
    private int port;

    public Client(String server, String userName, int port) {
        this.server = server;
        this.userName = userName;
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
