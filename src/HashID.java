// IN2011 Computer Networks
// Coursework 2023/2024
//
// Construct the hashID for a string

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashID {

	public static byte[] computeHashID(String line) throws Exception {
		if (line.endsWith("\n")) {
			// What this does and how it works is covered in a later lecture
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(line.getBytes(StandardCharsets.UTF_8));
			return md.digest();

		} else {
			// 2D#4 computes hashIDs of lines, i.e. strings ending with '\n'
			throw new Exception("No new line at the end of input to HashID");
		}
	}

	public static String byteToHex(String line) throws Exception // referenced from https://www.geeksforgeeks.org/java-program-to-convert-byte-array-to-hex-string/
	{
		byte[] byteArray;
		if(!line.endsWith("\n")){
			byteArray = HashID.computeHashID(line + "\n");
		}else {
			byteArray = HashID.computeHashID(line);
		}
		String hex = "";
		for (byte i : byteArray) {
			hex += String.format("%02X", i & 0xFF);
		}
		return hex.toLowerCase();
	}
	public static int calculateDistance(String s1, String s2){
		char[] c1 = s1.toCharArray();
		char[] c2 = s2.toCharArray();
		int x = 0;
		while(x < c1.length && c1[x] == c2[x]){
			x++;
		}
		if(x == c1.length){
			return 0;
		}
		int h1 = Character.digit(c1[x], 16);
		int h2 = Character.digit(c2[x], 16);
		String binaryString1 = Integer.toBinaryString(h1);
		String binaryString2 = Integer.toBinaryString(h2);
		while (binaryString1.length() < 4) {
			binaryString1 = "0" + binaryString1;
		}
		while (binaryString2.length() < 4) {
			binaryString2 = "0" + binaryString2;
		}
		String xOR = "";
		for(int i = 0; i < binaryString1.length(); i++){
			if(binaryString1.charAt(i) == binaryString2.charAt(i)){
				xOR += "0";
			}else { xOR += "1";}
		}
		return 256 - (Integer.parseInt(xOR,2) + (x * 4));
	}
	public static String otherhash(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(s.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {

				hexString.append(String.format("%02X", b));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String[] args) throws Exception {
		String s1 = "Hello World!";
		String s2 = "test/jabberwocky/1";
		System.out.println(HashID.computeHashID(s1 + "\n"));
	//	System.out.println(HashID.byteToHex(HashID.computeHashID(s1 + "\n")));
	//	System.out.println(HashID.byteToHex(HashID.computeHashID(s2 + "\n")));
		//System.out.println(calculateDistance(HashID.byteToHex(HashID.computeHashID(s1 + "\n")),HashID.byteToHex(HashID.computeHashID(s2 + "\n"))));
	//	System.out.println(HashID.otherhash("test/jabberwocky/1"));
	//	System.out.println(calculateDistance("0f033be6cea034bd45a0352775a219ef5dc7825ce55d1f7dae9762d80ce64411","0f0139b167bb7b4a416b8f6a7e0daa7e24a08172b9892171e5fdc615bb7f999b"));
		System.out.println(HashID.byteToHex("martin.brain@city.ac.uk:Martins-implementation-1.0,fullNode-20010" + "\n"));
		System.out.println(HashID.byteToHex("test/jabberwocky/4")+"\n");
	}
}