package httpServer.classFile;

import httpServer.conf.Setup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by lt on 2016/9/24.
 */
public class Log {
    public static final void requestLog(String con) throws IOException{
        HashMap setupConf= Setup.getMapping();
        String Home=setupConf.get("home").toString();
        String logUrl=setupConf.get("logFile").toString();
        File file=new File(Home+logUrl);
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fileLogWriter = new FileWriter(Home+logUrl, true);
        fileLogWriter.write(con+"\n");
        fileLogWriter.close();
    }
    public static final void errorLog(String con) throws IOException{

        HashMap setupConf=Setup.getMapping();
        String Home=setupConf.get("home").toString();
        String logUrl=setupConf.get("logErrorFile").toString();
        File file=new File(Home+logUrl);
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fileLogWriter = new FileWriter(Home+logUrl, true);
        fileLogWriter.write(con+"\n");
        fileLogWriter.close();
    }
}
