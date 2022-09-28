import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    int uniqueId;
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

    public void start(){
        keepGoing = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(keepGoing){
                display("Server waiting for Clients on port " + port + ".");
                Socket socket = serverSocket.accept();
                if (!keepGoing) {
                    break;
                    ClientThread thread = new ClientThread(socket);
                    al.add(thread);
                    thread.start();
                }
                try{
                    serverSocket.close();
                    for (int i = 0; i < al.size(); i++) {
                        ClientThead tc = al.get(i);
                        try{
                            tc.sInput.close();
                            tc.sOutput.close();
                            tc.socket.close();
                        }catch(IOException ioE){
                            ioE.printStackTrace();
                        }
                    }
                }catch(Exception e){
                    display("Exception closing the server and clients: " + e);
                }
            }
        }catch (IOException e){
            String message = sdf.format(new Date() + " Exception on new ServerSocket: " + e + "\n");
            display(message);
        }
    }

    /*public class ClientThread extends Thread{
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;
        String date;

        public ClientThread(Socket socket) {
            this.id = ++uniqueId;
            this.socket = socket;
            System.out.println("Thread trying to create Object ");
            try{
                this.sOutput = new ObjectOutputStream(socket.getOutputStream());
                this.sInput = new ObjectInputStream(socket.getInputStream());
                this.username = (String) sInput.readObject();
                broadcast(notif + username + " has joined the chatroom. " + notif);
            }catch(IOException e){
                display("Exception creating new Input / Output Streams: " + e);
                return;
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }*/

    private void display(String message) {
        String time = sdf.format(new Date()) + " " + message;
        System.out.println(time);
    }


}
