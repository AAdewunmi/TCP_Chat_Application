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
 * {@code @description} Server Class
 */


public class Server {

    int uniqueId;
    private final ArrayList<ClientThread> al;
    private final SimpleDateFormat sdf;
    private final int port;
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
                if (!keepGoing)
                    break;
                ClientThread thread = new ClientThread(socket);
                al.add(thread);
                thread.start();
            }
            try{
                serverSocket.close();
                for (ClientThread tc : al) {
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                }
            }catch(Exception e){
                display("Exception closing the server and clients: " + e);
            }
        }catch (IOException e){
            String message = sdf.format(new Date() + " Exception on new ServerSocket: " + e + "\n");
            display(message);
        }
    }

    protected void stop(){
        keepGoing = false;
        try{
            new Socket("localhost", port);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void display(String message) {
        String time = sdf.format(new Date()) + " " + message;
        System.out.println(time);
    }

    private synchronized boolean broadcast(String message){
        String time = sdf.format(new Date());
        String[] w = message.split(" ", 3);
        boolean isPrivate = w[1].charAt(0) == '@';

        if (isPrivate)
        {
            String tocheck = w[1].substring(1);
            message = w[0] + w[2];
            String messageLf = time + " " + message + "\n";
            boolean found = false;

            for (int y = al.size(); --y >= 0; ) {
                ClientThread ct1 = al.get(y);
                String check = ct1.getUsername();
                if (check.equals(tocheck)) {
                    if (!ct1.writeMsg(messageLf)) {
                        al.remove(y);
                        display("Disconnected Client " + ct1.username + " removed from list.");
                    }
                    found = true;
                    break;
                }
            }

            return found;
        }
        else
        {
            String messageLf = time + " " + message + "\n";
            System.out.println(messageLf);
            for (int i = al.size(); --i >= 0; ) {
                ClientThread ct = al.get(i);
                if (!ct.writeMsg(messageLf)) {
                    al.remove(i);
                    display("Disconnected Client " + ct.username + " removed from list");
                }
            }
        }
        return true;
    }

    synchronized void remove(int id){
        String disconnectedClient = "";
        for (int i = 0; i < al.size(); i++) {
            ClientThread ct = al.get(i);
            if (ct.id == id) {
                disconnectedClient = ct.getUsername();
                al.remove(i);
                break;
            }
        }
        broadcast(notif + disconnectedClient + " has left the chat room." + notif);
    }

    public static void main(String[] args) {
        int portNumber = 1500;
        switch (args.length){
            case 1:
                try{
                    portNumber = Integer.parseInt(args[0]);
                }catch(Exception e){
                    System.out.println("Invalid port number.");
                    System.out.println("Usage is: > Java Server [portNumber]");
                    e.printStackTrace();
                    return;
                }
            case 0:
                break;
            default:
                System.out.println("Usage is: > Java Server [portNumber]");
                return;
        }
        Server server = new Server(portNumber);
        server.start();
    }

    public class ClientThread extends Thread{
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
            System.out.println("Thread trying to create Object Object Input/Output Streams");
            try{
                this.sOutput = new ObjectOutputStream(socket.getOutputStream());
                this.sInput = new ObjectInputStream(socket.getInputStream());
                this.username = (String) sInput.readObject();
                broadcast(notif + username + " has joined the chatroom. " + notif);
            }catch(IOException e){
                display("Exception creating new Input / Output Streams: " + e);
                e.printStackTrace();
                return;
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            // date = new Date().toString() + "\n";
            date = new Date() + "\n";
        }

        public String getUsername() {
            return username;
        }

        public void run(){
            boolean keepGoing = true;
            while(keepGoing){
                try{
                    cm = (ChatMessage) sInput.readObject();
                }catch(IOException e){
                    display(username + " Exception reading Streams: " + e);
                    break;
                }catch(ClassNotFoundException e2){
                    e2.printStackTrace();
                    break;
                }
                String message = cm.getMessage();
                switch (cm.getType()){
                    case ChatMessage.MESSAGE:
                        boolean confirmation = broadcast(username + ": " + message);
                        if (!confirmation) {
                            String msg = notif + "Sorry. No such user exists. " + notif;
                            writeMsg(msg);
                        }
                        break;
                    case ChatMessage.LOGOUT:
                        display(username + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;
                    case ChatMessage.WHOISIN:
                        writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
                        for (int i = 0; i < al.size(); i++) {
                            ClientThread ct = al.get(i);
                            writeMsg((i + 1) + ") " + ct.username + " since " + ct.date);
                        }
                        break;
                }
            }
            remove(id);
            close();
        }

        private void close(){
            try{
                if (sOutput != null) {
                    sOutput.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            try{
                if (sInput!= null) {
                    sInput.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            try{
                if (socket!= null) {
                    socket.close();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        private boolean writeMsg(String msg) {
            if(!socket.isConnected()) {
                close();
                return false;
            }
            try {
                sOutput.writeObject(msg);
            }
            catch(IOException e) {
                display(notif + "Error sending message to " + username + notif);
                display(e.toString());
            }
            return true;
        }
    }// End of ClientThread Class

}// End of Server Class
