// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// YOUR_NAME_GOES_HERE
// YOUR_STUDENT_ID_NUMBER_GOES_HERE
// YOUR_EMAIL_GOES_HERE


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

// DO NOT EDIT starts
interface FullNodeInterface {
    boolean listen(String ipAddress, int portNumber);
    void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {
    HashMap<byte[], byte[]> keyValue;
    ServerSocket serverSocket;
    Socket clientSocket;
    BufferedReader recieve;
    Writer send;

    boolean start = false;
    public boolean listen(String ipAddress, int portNumber) {
        try {
            System.out.println("Opening the server socket on port " + portNumber);
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server waiting for client...");
            start = false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        // Implement this!
	// Return true if the node can accept incoming connections
	// Return false otherwise
	return true;
    }
    
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {
        try {
            if (!start) {
                clientSocket = serverSocket.accept();
                recieve = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String msg = recieve.readLine();
                if(msg.startsWith("START")) {
                    send = new OutputStreamWriter(clientSocket.getOutputStream());
                    send.write("START " + 1 + startingNodeName + "\n");
                    System.out.println("START " + 1 + startingNodeName);
                    send.flush();
                    System.out.println("node connected");
                    start = true;
                }else {
                    System.out.println("not connected");
                }
            }
            String msg = recieve.readLine();
            System.out.println(msg);
            if(msg.startsWith("PUT?")){
                String[] s = msg.split(" ");
                String key = "";
                String value = "";
                for(int x = 0; x < Integer.parseInt(s[1]); x++){
                    key += recieve.readLine();
                }
                for(int x = 0; x < Integer.parseInt(s[2]); x++){
                    value += recieve.readLine();
                }
                //byte[] k = new HashID().computeHashID(key);
                //byte[] v = new HashID().computeHashID(value);
                //keyValue.put(k,v);
                send.write("SUCCESS" + "\n");
                System.out.println("SUCCESS");
                send.flush();
            }
            if(msg.startsWith("GET?")){
                String[] s = msg.split(" ");
                String key = "";
                String value = "";
                for(int x = 0; x < Integer.parseInt(s[1]); x++){
                    key += recieve.readLine() + " ";
                }
                for(int x = 0; x < Integer.parseInt(s[2]); x++){
                    value += recieve.readLine() + " ";
                }
                send.write("VALUE");
            }
           /* System.out.println("here");
            String msg = recieve.readLine();
            String[] s = msg.split(" ");
            String key = null;
            String value = null;
            for(int x = 0; x < Integer.parseInt(s[1]); x++){
                key += recieve.readLine() + " ";
            }
            System.out.println("here");
            for(int x = 0; x < Integer.parseInt(s[2]); x++){
                value += recieve.readLine() + " ";
            }
            if(msg.startsWith("ECHO")){
                send.write("OHCE");
                send.flush();
            }else if(msg.startsWith("PUT?")){
                send.write("SUCCESS" + "\n");
            }else if(msg.startsWith("GET?")){

            }else if(msg.startsWith("NEAREST?")){

            }*/
        } catch(Exception e){
            System.out.println("error in full node");
            throw new RuntimeException(e);
        }
	// Implement this!
    }
}

