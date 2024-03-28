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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// DO NOT EDIT starts
interface FullNodeInterface {
    boolean listen(String ipAddress, int portNumber);
    void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {
    String name;
    String address;
    HashMap<Integer, ArrayList<String>> networkMap = new HashMap<>();

    HashMap<String, String> keyValue = new HashMap<String, String>();
    ServerSocket serverSocket;
    Socket clientSocket;
    BufferedReader recieve;
    BufferedWriter send;
    boolean connected = false;
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
            name = startingNodeName;
            address = startingNodeAddress;
            if(!start){
                start();
                connected = true;
            }
            while (connected) {
                String msg = recieve.readLine();
                System.out.println(msg);
                if (msg.startsWith("PUT?")) {
                    put(msg);
                } else if (msg.startsWith("GET?")) {
                    get(msg);
                } else if (msg.startsWith("NOTIFY")) {
                    notify(msg);
                } else if (msg.startsWith("NEAREST?")) {
                    send.write("NODES " + 3 + "\n");
                    send.write("name1" + "\n");
                    send.write("127.0.0.1:2345" + "\n");
                    send.write("name2" + "\n");
                    send.write("127.0.0.1:3456" + "\n");
                    send.write("name3" + "\n");
                    send.write("127.0.0.1:4567" + "\n");
                    send.flush();
                    System.out.println("it reaches");
                } else if (msg.startsWith("END")) {
                    clientSocket.close();
                    connected = false;
                }
            }

        } catch(Exception e){
            System.out.println("error in full node");
            throw new RuntimeException(e);
        }
	// Implement this!
    }
    public void start(){
        try{
        clientSocket = serverSocket.accept();
        String node = String.join(" ",name,address);
        networkMap.put(0,new ArrayList<String>());
        networkMap.get(0).add(String.join(" ",name,address));
        recieve = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String msg = recieve.readLine();
        if(msg.startsWith("START")) {
            send =new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            send.write("START " + 1 + name + "\n");
            System.out.println("START " + 1 + name);
            send.flush();
            System.out.println("node connected");
            start = true;
        }else {
            System.out.println("not connected");
        }
        }catch(Exception e){
            System.out.println("error in full node");
            throw new RuntimeException(e);
        }
    }
    public void put(String s){
        try{
            String[] strings = s.split(" ");
            String[] keys = new String[Integer.parseInt(strings[1])];
            String[] values = new String[Integer.parseInt(strings[2])];;
            for(int x = 0; x < Integer.parseInt(strings[1]); x++){
                keys[x] = recieve.readLine();
            }
            for(int x = 0; x < Integer.parseInt(strings[2]); x++){
                values[x] = recieve.readLine();
            }
            String key = String.join(" ",keys);
            String value = String.join(" ",values);
            //int distance = HashID.calculateDistance(HashID.byteToHex(HashID.computeHashID(key)))
            keyValue.put(key,value);
            send.write("SUCCESS" + "\n");
            send.flush();
        }catch(Exception e){
            System.out.println("error in store");
            throw new RuntimeException(e);
        }
    }
    public void get(String s){
        try{
            String[] strings = s.split(" ");
            String[] keys = new String[Integer.parseInt(strings[1])];
            for(int x = 0; x < Integer.parseInt(strings[1]); x++){
                keys[x] = recieve.readLine();
            }
            String key = String.join(" ",keys);
            String value = keyValue.get(key);
            if(value == null){
                send.write("NOPE" + "\n");
                System.out.println("NOPE");
                send.flush();
            }else {
                String[] values = value.split(" ");
                send.write("VALUE " + values.length + "\n");
                for (String v : values) {
                    send.write(v + "\n");
                }
                send.flush();
            }
        }catch(Exception e){
            System.out.println("error in get");
            throw new RuntimeException(e);
        }
    }
    public void notify(String s){
        try {
            String n = recieve.readLine();
            int x = HashID.calculateDistance(HashID.byteToHex(HashID.computeHashID(name + "\n")), HashID.byteToHex(HashID.computeHashID(n + "\n")));
            networkMap.put(x, new ArrayList<>());
            networkMap.get(x).add(String.join(" ", n, recieve.readLine()));
            send.write("NOTIFIED");
            send.flush();
        }catch(Exception e){
            System.out.println("error in Notify");
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        FullNode f = new FullNode();
        f.keyValue.put("hello there","does it work?");
        f.listen("127.0.0.1",1234);
        f.handleIncomingConnections("imranc@city.ac.uk","127.0.0.1:1234");
    }
    //test
}

