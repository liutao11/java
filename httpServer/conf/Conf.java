package httpServer.conf;

import java.util.HashMap;

/**
 * Created by lt on 2016/8/30.
 */
public class Conf {
    public final static HashMap<String,String> getUrlMapping(){
        HashMap<String,String>  urlMapping=new HashMap<String,String>();
        urlMapping.put("/Aworker/","Aworker");
        urlMapping.put("/AworkerGet/","AworkerGet");
        urlMapping.put("/Bworker/","Bworker");
        urlMapping.put("/BworkerGet/","BworkerGet");
        urlMapping.put("/Cworker/","Cworker");
        urlMapping.put("/Client/","Client");
        urlMapping.put("/ClientGet/","ClientGet");

        return urlMapping;
    }
}
