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
import java.security.KeyPair;
import java.util.*;

// DO NOT EDIT starts
interface FullNodeInterface {
    boolean listen(String ipAddress, int portNumber);
    void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {
    String startingName;
    String startingAddress;
    String ip;
    int port;
    HashMap<Integer, List<String>> networkMap = new HashMap<>();

    HashMap<String, String> keyValue = new HashMap<>();
    ServerSocket serverSocket;
    Socket clientSocket;
    Socket ssocket;
    BufferedReader recieve;
    PrintWriter send;
    boolean connected = false;
    boolean start = false;

    public boolean listen(String ipAddress, int portNumber) {
        try {
            ip = ipAddress;
            port = portNumber;
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
    
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) { // first point of contact in network
        try {
            startingName = startingNodeName;
            startingAddress = startingNodeAddress;
            if(!start){
                if(!(String.join(":", ip, String.valueOf(port)).equals(startingAddress))) {
                    System.out.println(start(startingName, startingAddress));
                    System.out.println(notify(startingName, startingAddress));
                    send.write("END notified");
                }
                respondStart();
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
                    respondNotify();
                } else if (msg.startsWith("NEAREST?")) {
                    String[] nodes = nearest(msg).split(" ");
                    send.write("NODES " + nodes.length/2 + "\n");
                    System.out.println("NODES " + nodes.length/2);
                    for (String s : nodes){
                        send.write(s + "\n");
                        System.out.println(s);
                    }
                    send.flush();
                } else if (msg.startsWith("ECHO")) {
                    send.write("OCHE" + "\n");
                    send.flush();
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
    public void addNode(String nodeName,String nodeAddress) throws Exception {
        int x = HashID.calculateDistance(HashID.byteToHex(HashID.computeHashID(nodeName + "\n")),HashID.byteToHex(HashID.computeHashID(nodeAddress + "\n")));
        if (!networkMap.containsKey(x)) {
            networkMap.put(x, new ArrayList<>());
        }
        if (!networkMap.get(x).contains(String.join(" ",nodeName,nodeAddress))) {
            if (networkMap.get(x).size() == 3) {
                networkMap.get(x).remove(2);
            }
            networkMap.get(x).add(String.join(" ", nodeName, nodeAddress));
        }
    }

    public boolean start(String connectingNodeName,String connectingNodeAddress){
        System.out.println("Full Node connecting to " +  connectingNodeName);
        String[] addrs = connectingNodeAddress.split(":");
        try {
            ssocket = new Socket(addrs[0],Integer.parseInt(addrs[1]));
            recieve = new BufferedReader(new InputStreamReader(ssocket.getInputStream()));
            send = new PrintWriter((new OutputStreamWriter(ssocket.getOutputStream())),true);
            send.write("START 1 " + startingName + "\n");
            send.flush();
            System.out.println("START 1 "  + startingName);
            String msg = recieve.readLine();
            System.out.println(msg);
            if(!msg.startsWith("START")){
                send.close();
                recieve.close();
                ssocket.close();
                return false;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }



    public void respondStart(){
        try{
        clientSocket = serverSocket.accept();
        recieve = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String msg = recieve.readLine();
        if(msg.startsWith("START")) {
            send =new PrintWriter((new OutputStreamWriter(clientSocket.getOutputStream())));
            send.write("START " + 1 + " " + startingName + "\n");
            System.out.println("START " + 1 + startingName);
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
            System.out.println(Arrays.toString(strings));
            String[] keys = new String[Integer.parseInt(strings[1])];
            for(int x = 0; x < Integer.parseInt(strings[1]); x++){
                keys[x] = recieve.readLine();
            }
            String key = String.join(" ",keys);
            if(!(nearest("NEAREST? " + HashID.byteToHex(HashID.computeHashID(key))).contains(startingName + " " + startingAddress))){
                send.write("FAILED" + "\n");
                send.flush();
            }else {
                String[] values = new String[Integer.parseInt(strings[2])];
                for (int x = 0; x < Integer.parseInt(strings[2]); x++) {
                    values[x] = recieve.readLine();
                }
                String value = String.join(" ", values);
                keyValue.put(key, value);
                send.write("SUCCESS" + "\n");
                send.flush();
            }
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
    public void respondNotify(){
        try {
            String name = recieve.readLine();
            String address = recieve.readLine();
            addNode(name,address);
            send.write("NOTIFIED" + "\n");
            send.flush();
            System.out.println("NOTIFIED");
        }catch(Exception e){
            System.out.println("error in Notify");
            throw new RuntimeException(e);
        }
    }
    public String nearest(String msg){
        try {
            String hashID = msg.split(" ")[1];
            ArrayList<String> ordered = new ArrayList<>();
            System.out.println(networkMap.size());
            for (Integer distance : networkMap.keySet()) {
                for (String node : networkMap.get(distance)){
                    String name = node.split(" ")[0];
                    int d = HashID.calculateDistance(HashID.byteToHex(HashID.computeHashID(name)),hashID);
                    ordered.add(String.join(" ",String.valueOf(d) ,node));
                }
            }
            Collections.sort(ordered, (s1, s2) -> {
                int num1 = Integer.parseInt(s1.split(" ")[0]);
                int num2 = Integer.parseInt(s2.split(" ")[0]);
                return Integer.compare(num1, num2);
            });
            String[] nodes = new String[3];
            for(int i = 0; i < 3;i++){
                if(ordered.get(i) != null) {
                    nodes[i] = ordered.get(i).split(" ", 2)[1];
                }
            }
            return String.join(" ",nodes);
            } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public boolean notify(String nodeName,String nodeAddress) throws IOException {
        send.write("NOTIFY" + "\n");
        System.out.println("NOTIFY");
        send.write(startingName + "\n");
        System.out.println(startingName);
        send.write(startingAddress + "\n");
        System.out.println(startingAddress);
        send.flush();
        String response = recieve.readLine();
        System.out.println(response);
        return Objects.equals(response, "NOTIFIED");
    }



    public static void main(String[] args) throws Exception {
        FullNode fn1 = new FullNode();
        fn1.addNode("imran:node-1","127.0.0.1:1234");
        fn1.addNode("imran:node-2","127.0.0.1:2345");
        fn1.addNode("imran:node-3","127.0.0.1:3456");
        fn1.addNode("imran:node-4","127.0.0.1:4567");
        fn1.addNode("imran:node-5","127.0.0.1:5678");
        //fn1.keyValue.put("hello there", "does it work?");
        fn1.listen("127.0.0.1",1234);
        fn1.handleIncomingConnections("imran:node-1","127.0.0.1:1234");
    }
    //test
}

