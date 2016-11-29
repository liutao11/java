package httpServer.classFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lt on 2016/10/26.
 */
public class Mmd5 {
    public static String createString(String md5str){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(md5str.getBytes());
            byte[] b = md5.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            String mySign = buf.toString();
            return mySign;
        }catch (NoSuchAlgorithmException NoshuchE){
            return "";
        }
    }
}
