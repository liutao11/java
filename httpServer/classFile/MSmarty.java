package httpServer.classFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lt on 2016/9/10.
 */
public class MSmarty{
    private String htmlfile;
    private HashMap<String,String> assign=new HashMap<String,String>();
    public   MSmarty(String htmlfile){
        this.htmlfile=htmlfile;
    }
    public void assign(String key,String value){
        this.assign.put(key,value);
    }
    public String display(){
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(this.htmlfile));
            String fileReaderLione=null;
            String fileConnet="";
            while ((fileReaderLione=fileReader.readLine())!=null){
                fileConnet+=fileReaderLione;
            }
            fileReader.close();

            Iterator<String> iterator=this.assign.keySet().iterator();
            while (iterator.hasNext()){
                String key=iterator.next();
                fileConnet=fileConnet.replaceAll("\\<\\{\\$"+key+"\\}\\>",this.assign.get(key));
            }
            return fileConnet;
        }catch (Exception e) {
            System.out.print(e);
            return "1";
        }
    }
}
