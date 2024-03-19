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
    Writer send;
    BufferedReader recieve;
    Socket socket;
    public boolean start(String startingNodeName, String startingNodeAddress) {
        System.out.println("TCPClient connecting to " +  startingNodeAddress);
        String[] addrs = startingNodeAddress.split(":");
        try {
           socket = new Socket(addrs[0],Integer.parseInt(addrs[1]));
           recieve = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           send = new OutputStreamWriter(socket.getOutputStream());
           send.write("START 1 " + 1 + startingNodeName + "\n");
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
            String response = recieve.readLine();//ERROR HERE
            System.out.println(response);
            return Objects.equals(response, "SUCCESS");
        } catch (IOException e) {
            System.out.println("error in temp");
            return false;
        }
    }

    public String get(String key) {
        try {
            String[] keys = key.split(" ");
            if(keys.length == 0){
                return null;
            }
            send.write("GET? " + keys.length + "\n");
            for(String s : keys){
                send.write(s + "\n");
            }
            send.flush();
            String response = recieve.readLine();
            if(Objects.equals(response, "NOPE")){
                return null;
            }
            String value = recieve.readLine();
            return value;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public String nearest(String key){
        try {
            send.write("NEAREST? " + Arrays.toString(new HashID().computeHashID(key)) + "\n");
            send.flush();
            recieve.readLine();
            return recieve.readLine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        TemporaryNode tn = new TemporaryNode();
        if(tn.start("imranc@city.ac.uk","127.0.0.1:4567")){
            System.out.println("connected");
        }
        if(tn.store("hello there","does it work?")){
            System.out.println("it works");
        }
    }
}
