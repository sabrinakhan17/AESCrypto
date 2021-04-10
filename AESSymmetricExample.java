import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.security.SecureRandom;
/**
 * Basic symmetric encryption example for demo purposes only without changing provider (e.g. BouncyCastle); 
 * it may not conform to current best practices as reported by NIST
 * https://csrc.nist.gov/projects/cryptographic-standards-and-guidelines
 */
public class AESSymmetricExample {   
    private static SecretKeySpec secretKey;
    private static byte[] key;
    
    public static void main(String[] args) throws Exception {
        
        //Original AES Encryption Example:
    	  String plaintext = "Sensitive information";
        String phonykey = "phonykey"; //weak key !!
        
        System.out.println("input text : " + plaintext);
        System.out.println("input text : " + phonykey);
        
        String encryptedString = encrypt(plaintext, phonykey);      
        System.out.println("cipher text: " + encryptedString);
        
        String decryptedString = decrypt(encryptedString, phonykey);
        System.out.println("plain text : " + decryptedString );
        
        //AES/GCM/NoPadding Example:
        System.out.println("\nAES/GCM/NoPadding Example:\n");
        
        byte[] IV = new byte[12];
        System.out.println("input text : " + plaintext);
        System.out.println("input text : " + phonykey);
        
        byte[] encrypted = encryptAESGCM(plaintext.getBytes(), phonykey, IV);
        System.out.println("cipher text: " + Base64.getEncoder().encodeToString(encrypted));
        
        String decryptedText = decryptAESGCM(encrypted, phonykey, IV);
        System.out.println("plain text : " + decryptedText );
                
    }
     
    public static void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); 
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
        
    public static void moreRandomSetKey() {
      try {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        secretKey = new SecretKeySpec(key, "AES");
      } catch (Exception e) {
        e.printStackTrace();
      }
	}
    
 
    public static String encrypt(String strToEncrypt, String secret) {
        try{
            //setKey(secret);
            moreRandomSetKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }catch (Exception e){
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
 
    public static String decrypt(String strToDecrypt, String secret){
        try{
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }catch (Exception e){
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
    
    public static byte[] encryptAESGCM(byte[] plaintext, String key, byte[] IV) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        setKey(key);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, IV);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] cipherText = cipher.doFinal(plaintext);
        return cipherText;
    }

    public static String decryptAESGCM(byte[] cipherText, String key, byte[] IV) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        setKey(key);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, IV);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] decryptedText = cipher.doFinal(cipherText);
        return new String(decryptedText);
    }
    
}
