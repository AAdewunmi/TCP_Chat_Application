import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Adrian Adewunmi
 * {@code @date} 25 Sept 2022
 * {@code @description} Client Class
 */
public class Client {

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;
    private final String server;
    private String userName;
    private final int port;

    public Client(String server, int port, String userName) {
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
        new ListenFromServer().start();
        try{
            sOutput.writeObject(userName);
        }catch(IOException e){
            display("Exception performing login " + e);
            disconnect();
            return false;
        }
        return true;
    }

    private void display(String message){
        System.out.println(message);
    }

    void sendMessage(ChatMessage message) {
        try {
            sOutput.writeObject(message);
        }
        catch(IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    private void disconnect(){
        try{
            if (sInput != null) {
                sInput.close();
            }
        }catch (Exception e){}
        try{
            if (sOutput != null) {
                sOutput.close();
            }
        }catch (Exception e){}
        try{
            if (socket != null) {
                socket.close();
            }
        }catch (Exception e){}
    }

    public static void main(String[] args) {

        // Localhost Connection
        int portNumber = 1500;
        String serverAddress = "localhost";

        // Ngrok Connection
        /*int portNumber = 17964;
        String serverAddress = "0.tcp.ngrok.io";*/

        // String userName = "Anonymous";
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter User Name: ");
        String userName = scanner.nextLine();

        switch(args.length){
            case 3:
                serverAddress = args[2];
            case 2:
                try{
                    portNumber = Integer.parseInt(args[1]);
                }catch (Exception e){
                    System.out.println("Invalid port number. ");
                    System.out.println("Usage is: > Java Client [username] [portNumber] [serverAddress]");
                    return;
                }
            case 1:
                userName = args[0];
            case 0:
                break;
            default:
                System.out.println("Usage is: > Java Client [username] [portNumber] [serverAddress]");
                return;
        }

        Client client = new Client(serverAddress, portNumber, userName);

        if (!client.start()) {
            return;
        }

        System.out.println("\nHello. " + userName + "! Welcome to the chatroom.");
        System.out.println("Instruction: ");
        System.out.println("1. Simply type the message to send broadcast to all active clients");
        System.out.println("2. Type '@username<space>your message' without quotes to send message to desired client");
        System.out.println("3. Type 'WHOISIN' without quotes to see list of active clients");
        System.out.println("4. Type 'LOGOUT' without quotes to logoff from server");

        while(true){
            System.out.println(" > ");
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("LOGOUT")) {
                client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, "Logging you out ... "));
                break;
            }else if(message.equalsIgnoreCase("WHOISIN")){
                client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, "Who is this? ... "));
            }else{
                client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, message));
            }
        }

        scanner.close();
        client.disconnect();

    }// End of main

    class ListenFromServer extends Thread{

        public void run(){
            while(true){
                try{
                    String message = (String) sInput.readObject();
                    System.out.println(message);
                    System.out.println("> ");
                }catch(IOException e){
                    String notif = " *** ";
                    display(notif + " Server has closed the connection: " + e + notif);
                    break;
                }catch(ClassNotFoundException e2){
                }
            }
        }

    }
}
