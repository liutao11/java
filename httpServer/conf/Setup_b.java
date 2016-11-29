package httpServer.conf;

import java.util.HashMap;

/**
 * Created by lt on 2016/9/10.
 */
public class Setup_b {
    public final static HashMap<String,String> getMapping(){
        HashMap<String,String>  setupMapping=new HashMap<String,String>();
        setupMapping.put("serverPort","9364");                                          //服务器监听端口
        setupMapping.put("home","./httpServer");                                        //相对路径
        setupMapping.put("logFile","/httpServer.log");                                 //日志路径
        setupMapping.put("logErrorFile","/errorServer.log");                                 //日志路径
        setupMapping.put("DBurl","127.0.0.1");
        setupMapping.put("DBport","8723");
        setupMapping.put("DBuser","root");
        setupMapping.put("DBtable","PhoneOtherServer");
        setupMapping.put("DBpassword","4se@q15wAe_63rl&Hs9u");
        return setupMapping;
    }

}
