// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// YOUR_NAME_GOES_HERE
// YOUR_STUDENT_ID_NUMBER_GOES_HERE
// YOUR_EMAIL_GOES_HERE


import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    boolean start(String startingNodeName, String startingNodeAddress);
    boolean store(String key, String value);
    String get(String key);
}
// DO NOT EDIT ends


public class TemporaryNode implements TemporaryNodeInterface {
    PrintWriter send;
    BufferedReader recieve;
    Socket socket;
    public boolean start(String startingNodeName, String startingNodeAddress) {
        System.out.println("Temporary Node connecting to " +  startingNodeAddress);
        String[] addrs = startingNodeAddress.split(":");
        try {
            socket = new Socket(addrs[0],Integer.parseInt(addrs[1]));
            recieve = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            send = new PrintWriter((new OutputStreamWriter(socket.getOutputStream())),true);
            send.write("START 1 "  + startingNodeName + "\n");
            send.flush();
            System.out.println("START 1 "  + startingNodeName);
            String msg = recieve.readLine();
            System.out.println(msg);
            if(!msg.startsWith("START")){
                send.close();
                recieve.close();
                socket.close();
                return false;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            end();
            return false;
        }
        return true;
    }
    public boolean store(String key, String value) {
        String[] nodes = nearest(key);
        if(storeValue(key,value)){
            return true;
        }
        System.out.println(Arrays.toString(nodes));
        end();
        for(int i = 0; i < nodes.length; i+=2){
            start(nodes[i],nodes[i+1]);
            if(storeValue(key,value)){
                return true;
            }
            end();
        }
        return false;
    }
    public boolean storeValue(String key, String value){
        try {
            String[] keys = key.split(" ");
            String[] values = value.split(" ");
            send.write("PUT? " + keys.length + " " + values.length + "\n");
            System.out.println("PUT? " + keys.length + " " + values.length);
            for(String s : keys){
                System.out.println(s);
                send.write(s + "\n");
            }
            for(String s : values){
                System.out.println(s);
                send.write(s + "\n");
            }
            send.flush();
            String response = recieve.readLine();
            System.out.println(response);
            return Objects.equals(response, "SUCCESS");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            end();
            return false;
        }
    }

    public String get(String key) {
        try {
            String[] nodes = nearest(key);
            String value = getValue(key);
            if(value != null){
                return value;
            }
            end();
            for(int i = 0; i < nodes.length; i+=2){
                start(nodes[i],nodes[i+1]);
                value = getValue(key);
                if(value != null){
                    return value;
                }
                end();
              /*  if(i == nodes.length-2){
                    get(key);
                }else {
                    end();
                } */
            }
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            end();
            return null;
        }
    }
    public String getValue(String key){
        try{
            String[] keys = key.split(" ");
            if (keys.length == 0) {
                return null;
            }
            send.write("GET? " + keys.length + "\n");
            System.out.println("GET? " + keys.length);
            for (String s : keys) {
                send.write(s + "\n");
                System.out.println(s);
            }
            send.flush();
            String[] responses = recieve.readLine().split(" ");
            if (Objects.equals(responses[0], "VALUE")) {
                System.out.println(responses[0] + " " + responses[1]);
                String[] values = new String[Integer.parseInt(responses[1])];
                for (int i = 0; i < Integer.parseInt(responses[1]); i++) {
                    values[i] = recieve.readLine();
                    System.out.println(values[i]);
                }
                return String.join(" ",values);
            }
            System.out.println(responses[0]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            end();
            return null;
        }
        return null;
    }
    public String[] nearest(String key) {
        try{
            send.write("NEAREST? " + HashID.byteToHex(HashID.computeHashID(key))+"\n");
            System.out.println("NEAREST? " + HashID.byteToHex(HashID.computeHashID(key)));
            send.flush();
            String[] response = recieve.readLine().split(" ");
            if(!(Objects.equals(response[0], "NODES"))){
                return null;
            }
            System.out.println(response[0] + " " + response[1]);
            String[] nodes = new String[Integer.parseInt(response[1]) * 2];
            for (int i = 0; i < Integer.parseInt(response[1]) * 2; i++) {
                nodes[i] = recieve.readLine();
                System.out.println(nodes[i]);
            }
            return nodes;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            end();
            return null;
        }
    }
    public String findClosestNode(String key) throws Exception {
        String keyHashID = HashID.byteToHex(HashID.computeHashID(key));
        String closestNode = null;
        long closestDistance = Long.MAX_VALUE;
        String[] nodes = nearest(key);
        for (int i = 0; i < nodes.length; i += 2) {
            String nodeName = nodes[i];
            String nodeAddress = nodes[i + 1];

        }
        return closestNode;
    }


    public void end(){
        try{
            send.write("END " + "stop" + "\n");
            send.flush();
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            end();
        }
    }


    public static void main(String[] args) throws Exception {
        TemporaryNode tn = new TemporaryNode();
        if(tn.start("imran:node-1","127.0.0.1:1234")){
            System.out.println("connected");
        }
        if(tn.store("hello there","does it work?")){
         System.out.println("it works");
         }
        //System.out.println(tn.getClosestNode("hello there"));
        //tn.get("test/jabberwocky/4" + "\n");
        //tn.findClosestNode("hello");
        //tn.nearest("hello hello");
        tn.end();
    }
}
