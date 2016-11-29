package httpServer.conf;

import java.util.HashMap;

/**
 * Created by lt on 2016/9/10.
 */
public class Setup {
    public final static HashMap<String,String> getMapping(){
        HashMap<String,String>  setupMapping=new HashMap<String,String>();
        setupMapping.put("serverPort","9364");                                          //服务器监听端口
        setupMapping.put("home","./httpServer");                                        //相对路径
        setupMapping.put("logFile","/httpServer.log");                                 //日志路径
        setupMapping.put("logErrorFile","/errorServer.log");                                 //日志路径
        setupMapping.put("DBurl","172.16.3.161");
        setupMapping.put("DBport","3306");
        setupMapping.put("DBuser","broot");
        setupMapping.put("DBtable","PhoneOtherServer");
        setupMapping.put("DBpassword","123456");
        return setupMapping;
    }

}
