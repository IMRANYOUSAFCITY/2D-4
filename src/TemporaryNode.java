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
           String msg = recieve.readLine();
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
            end();
            for(String s : nodes){
                start("name",s);
                String value = getValue(key);
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
                return String.join(" ", values);
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
            send.write("NEAREST? " + HashID.otherhash(key) + "\n");
            //System.out.println("NEAREST? " + HashID.otherhash(key) + "\n");
            send.flush();
            if(!recieve.readLine().startsWith("NODES")){
                return null;
            }
            String[] addrs = new String[3];
            String[] names = new String[3];
            for (int i = 0; i < 3; i++) {
                names[i] = recieve.readLine();
                addrs[i] = recieve.readLine();
                System.out.println(addrs[i]);
            }
            return addrs;
            } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("ERROR in Nearest method");
            return null;
        }
    }
    public String getClosestNode(String key) {
        try{
            send.write("NEAREST? " + HashID.otherhash(key) + "\n");
            //System.out.println("NEAREST? " + HashID.byteToHex(HashID.computeHashID(key + "\n")));
            send.flush();
            if(!recieve.readLine().startsWith("NODES")){
                return null;
            }
            return String.join(" ",recieve.readLine(),recieve.readLine());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("ERROR in Nearest method");
            return null;
        }
    }
    public void end(){
        try{
        send.write("END " + "stop");
        send.flush();
        socket.close();
    } catch (IOException e) {
        System.out.println(e.getMessage());
        System.out.println("ERROR in Nearest method");
    }
    }

    /*{
        try {
            String[] node;
            int x = 0;
            while (x < 3) {
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
                System.out.println(responses[0]);
                String[] addrs = new String[3];
                if (Objects.equals(responses[0], "VALUE")) {
                    String[] values = new String[Integer.parseInt(responses[1])];
                    for (int i = 0; i < Integer.parseInt(responses[1]); i++) {
                        values[i] = recieve.readLine();
                        System.out.println(values[i]);
                    }
                    return String.join(" ", values);
                } else if (Objects.equals(responses[0], "NOPE") && x == 0) {
                    send.write("NEAREST? " + HashID.otherhash(key + "\n"));
                    System.out.println("NEAREST? " + HashID.otherhash(key + "\n"));
                    send.flush();
                    for (int i = 0; i < 3; i++) {
                        addrs[i] = String.join(":",recieve.readLine(),recieve.readLine());
                        System.out.println(addrs[i]);
                    }
                }
                node = addrs[x].split(":");
                socket = new Socket(node[0],Integer.parseInt(node[1]));
                recieve = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                send =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                x++;    System.out.println(x);
            }
            return null;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("ERROR in temp node");
            return null;
        }
    } */


    public static void main(String[] args) throws Exception {
        TemporaryNode tn = new TemporaryNode();
        if(tn.start("imranc@city.ac.uk","127.0.0.1:1234")){
            System.out.println("connected");
        }
       //if(tn.store("hello there","does it work?")){
         //  System.out.println("it works");
        //}
        //System.out.println(tn.getClosestNode("hello there"));
        tn.get("hello there");
        //tn.nearest("hello There");
        tn.end();
    }
}
