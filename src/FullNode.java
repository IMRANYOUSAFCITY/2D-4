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
    HashMap<String, String> keyValue = new HashMap<String, String>();
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
            if(msg.startsWith("PUT?")){
                String[] s = msg.split(" ");
                String key = "";
                String value = "";
                for(int x = 0; x < Integer.parseInt(s[1]); x++){
                    key += recieve.readLine() + " ";
                }
                for(int x = 0; x < Integer.parseInt(s[2]); x++){
                    value += recieve.readLine() + " ";
                }
                //byte[] k = new HashID().computeHashID(key+"\n");
                //byte[] v = new HashID().computeHashID(value+"\n");
                keyValue.put(key,value);
                send.write("SUCCESS" + "\n");
                send.flush();
            } else if(msg.startsWith("GET?")){
                String[] s = msg.split(" ");
                String[] keys = new String[Integer.parseInt(s[1])];
                for(int x = 0; x < Integer.parseInt(s[1]); x++){
                   keys[x] = recieve.readLine();
                }
                String key = String.join(" ",keys);
                String value = keyValue.get(key);
                String[] values = value.split(" ");
                send.write("VALUE " + values.length + "\n");
                for(String v :values){
                    send.write(v + "\n");
                }
                send.flush();
            }

        } catch(Exception e){
            System.out.println("error in full node");
            throw new RuntimeException(e);
        }
	// Implement this!
    }

    public static void main(String[] args) {
        FullNode f = new FullNode();
        f.keyValue.put("hello there","does it work?");
        f.listen("127.0.0.1",4567);
        f.handleIncomingConnections("imranc@city.ac.uk","127.0.0.1:4567");
    }
}

