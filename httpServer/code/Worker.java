package httpServer.code;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by lt on 2016/8/31.
 */
public abstract class Worker{
    public ChannelHandlerContext ctx;
    public String method;
    public String url;
    public String session;
    public String getContent;
    public HashMap<String,String> GET=new HashMap<String,String>();
    public HashMap<String,String> POST=new HashMap<String,String>();
    public String postContent;
    public HashMap <String,String > Setup;
    public String home;
    public HttpHeaders headers;
    public String requestUnid;
    public abstract String start() throws Exception;
    public static LinkedList<String> history=new LinkedList<String>();
}
