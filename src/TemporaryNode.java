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
           long t = System.currentTimeMillis();
           String name = t+"";
           send.write("START 1 "  + name + "\n");
           send.flush();
           System.out.println("START 1 "  + name);
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
            System.out.println("error in temp");
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
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
            System.out.println("ERROR in GetValue node");
            return null;
        }
        return null;
    }
    public String[] nearest(String key) {
        try{
            send.write("NEAREST? " + HashID.byteToHex(key)+"\n");
            System.out.println("NEAREST? " + HashID.byteToHex(key));
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
            System.out.println("ERROR in Nearest method");
            return null;
        }
    }
    public String getClosestNode(String key) {
        try{
           // send.write("NEAREST? " + HashID.byteToHex(HashID.computeHashID(key))+"\n");
            //System.out.println("NEAREST? " + HashID.byteToHex(HashID.computeHashID(key + "\n")));
            send.flush();
            if(!recieve.readLine().startsWith("NODES")){
                return null;
            }
            String closestName = recieve.readLine();
            String closestAddress = recieve.readLine();
            while (true){
                end();
                start(closestName,closestAddress);
               // send.write("NEAREST? " + HashID.byteToHex(HashID.computeHashID(key))+"\n");
                send.flush();
                if(!recieve.readLine().startsWith("NODES")){
                    return null;
                }
                String nearName = recieve.readLine();
                String nearAddress = recieve.readLine();
                if(Objects.equals(nearName, closestName) && Objects.equals(nearAddress, closestAddress)){
                    end();
                    return String.join(" ",closestName,closestAddress);
                }else {
                    closestName = nearName;
                    closestAddress = nearAddress;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("ERROR in Nearest method");
            return null;
        }
    }
    public void end(){
        try{
        send.write("END " + "stop" + "\n");
        send.flush();
        socket.close();
    } catch (IOException e) {
        System.out.println(e.getMessage());
        System.out.println("ERROR in END method");
    }
    }


    public static void main(String[] args) {
        TemporaryNode tn = new TemporaryNode();
        if(tn.start("string","127.0.0.1:1234")){
            System.out.println("connected");
        }
       if(tn.store("test/jabberwocky/4","does it work?")){
          System.out.println("it works");
        }
        System.out.println(tn.getClosestNode("hello there"));
        //tn.get("test/jabberwocky/4");
        //tn.nearest("hello hello");
        tn.end();
    }
}
