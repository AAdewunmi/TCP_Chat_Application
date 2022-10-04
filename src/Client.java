import java.io.IOException;
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

    public boolean start(){
        try{
            socket = new Socket(server, port);
        }catch(Exception e){
            display("Error connecting to server: " + e);
            return false;
        }

        String message = "Connection accepted " + socket.getInetAddress() + " : " + socket.getPort();
        display(message);

        try{
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }catch(IOException e){
            display("Exception creating new Input/Output Streams: " + e);
            return false;
        }
        new ListenFromServer.start();
        try{
            sOutput.writeObject(userName);
        }catch(IOException e){
            display("Exception performing login " + e);
            disconect();
            return false;
        }
        return true;
    }

    private void display(String message){
        System.out.println(message);
    }
}
