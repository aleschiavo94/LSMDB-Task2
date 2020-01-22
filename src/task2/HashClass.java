package task2;
// credit for this class to http://www.sha1-online.com/sha1-java/
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashClass {
	public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(sha1("test string to sha1"));
    }
     
    static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
    }
    
    public static String convertToSha(String input) {
    	try {
    		input = HashClass.sha1(input);
        }catch(NoSuchAlgorithmException e) {
        	e.printStackTrace();
        }
    	return input;
    }
}