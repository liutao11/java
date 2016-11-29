package httpServer.classFile;

import java.util.Random;

/**
 *生成随机字符串
 */
public  class RandStr {
    public static String  worker(int length){
        String base="abcdefghijklmnopqrstuvwxyz0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
        String requestUnid ="";
        for (int i=0;i<length;i++){
            int index=new Random().nextInt(base.length());
            requestUnid+=base.charAt(index);
        }
         return requestUnid;
    }
}
