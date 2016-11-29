package httpServer.classFile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * Created by lt on 2016/9/17.
 */
public class JsSend {
    public  static  String  readJsOut (String ImageFileUrl){
        String fileConnet="";
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(ImageFileUrl));
            String fileReaderLione=null;

            while ((fileReaderLione=fileReader.readLine())!=null){
                fileConnet+=fileReaderLione;
            }
            fileReader.close();
        }catch (Exception e){
            System.out.println(e);
        }
        return  fileConnet;
    }

}
