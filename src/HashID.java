// IN2011 Computer Networks
// Coursework 2023/2024
//
// Construct the hashID for a string

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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

	public static String byteToHex(byte[] byteArray) // referenced from https://www.geeksforgeeks.org/java-program-to-convert-byte-array-to-hex-string/
	{
		String hex = "";
		for (byte i : byteArray) {
			hex += String.format("%02X", i & 0xFF);
		}
		return hex;
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


	public static void main(String[] args) throws Exception {
		String s1 = "Hello World!";
		String s2 = "test/jabberwocky/1";
		System.out.println(HashID.computeHashID(s1 + "\n"));
		System.out.println(HashID.byteToHex(HashID.computeHashID(s1 + "\n")));
		System.out.println(HashID.byteToHex(HashID.computeHashID(s2 + "\n")));
		//System.out.println(calculateDistance(HashID.byteToHex(HashID.computeHashID(s1 + "\n")),HashID.byteToHex(HashID.computeHashID(s2 + "\n"))));

	}
}