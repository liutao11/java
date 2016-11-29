package httpServer;

import httpServer.classFile.*;
import httpServer.classFile.FaviconImg;
import httpServer.classFile.ImageSend;
import httpServer.classFile.JsSend;
import httpServer.classFile.RandStr;
import httpServer.code.Worker;
import httpServer.code.WebWorker;
import httpServer.conf.Conf;
import httpServer.conf.Setup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.ByteArrayOutputStream;

import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.*;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import httpServer.classFile.Log;

import httpServer.worker.Aworker;
import httpServer.worker.AworkerGet;

/**
 * Created by lt on 2016/8/27.
 */
public class HttpServer {
    public static final void  main(String[] args) throws Exception{

        int port=Integer.parseInt(Setup.getMapping().get("serverPort"));
        EventLoopGroup boss=new NioEventLoopGroup();
        EventLoopGroup work=new NioEventLoopGroup();
        ServerBootstrap bootstrap=new ServerBootstrap();
        bootstrap.group(boss,work).channel(NioServerSocketChannel.class).childHandler(new HttpServerHandler());
        try {
            ChannelFuture fb = bootstrap.bind(port).sync();
            System.out.println("************************************************************");
            System.out.println("httpServer is worker,this port is "+port+", this is running .....");
            System.out.println("************************************************************");
            fb.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}

class HttpServerHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected  void initChannel(SocketChannel ch) throws Exception{
        ch.pipeline().addLast(new HttpResponseEncoder());
        ch.pipeline().addLast(new HttpRequestDecoder());
        //ch.pipeline().addLast(new ChunkedWriteHandler());
        ch.pipeline().addLast(new HttpObjectAggregator(1048576));
        ch.pipeline().addLast(new HttpContentCompressor());
        ch.pipeline().addLast(new HttpServerHandlerWorker());
    }
}


/*class HttpServerHandlerWorker extends ChannelInboundHandlerAdapter{*/

class HttpServerHandlerWorker extends SimpleChannelInboundHandler<FullHttpRequest>{
    private String url;
    private String method;
    private String requestUnid;
    private FileWriter fileLogWriter;
    private HttpHeaders headers;
    private String getContent=null;               //get提交过来参数
    private HashMap<String,String> Cookie=new HashMap<String,String>();
    private HashMap<String ,String> postContent=new HashMap<String ,String>();
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //生成一个链接唯一编码
        System.out.println("channelActive");
        String requestUnid = RandStr.worker(10);
        this.requestUnid=requestUnid;
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception{
        if(msg instanceof  FullHttpRequest){
            FullHttpRequest request =(FullHttpRequest) msg;
            this.headers=request.headers();
            //请求方式get还是post
            this.method=request.method().toString();
            String uri = request.uri();
            //判断uri中是否包括？
            int uriStateInt=uri.indexOf('?');
            String[] uri_array=null;

            System.out.println(uri);


            if(uriStateInt>=0) {                      //uri 中包括问号
                uri_array = uri.split("[?]");
                this.url = uri_array[0];
                if(uri_array.length>1) {
                    this.getContent = uri_array[1];
                }
            }else{                                   //uri中不包括问号
                this.url=uri;
                uri_array=new String[2];
                uri_array[0]=uri;
            }
            //保存到日志中

            String address=ctx.channel().remoteAddress().toString();
            String method=request.method().name();
            Log.requestLog("unid:" + this.requestUnid + " address:" + address + " Url:" + this.url + " method:" + method + " getCont:" + this.getContent + " CTime:" + new Date().getTime());
            //得到body内容
            String postContent_str=request.content().toString(Charset.forName("UTF-8"));
            String UNID=null;
            if(!this.Cookie.containsKey("SESSIONID")){
                UNID=RandStr.worker(30);
            }else{
                UNID=this.Cookie.get("SESSIONID");
            }
            String res;
            HashMap<String,String> urlMapping= Conf.getUrlMapping();
            String urlEnd=this.url.substring(this.url.length()-1);       //url最后一位
            String urlEndPhp=null;
            if(this.url.length()>5) {                                   //超度超过5位的时候，兼容“.php”
                urlEndPhp = this.url.substring(this.url.length() - 4);
            }
            if(urlEnd.equals("/") || urlEndPhp.equals(".php")) {
                if (urlMapping.containsKey(this.url)) {
                    String urlInto = urlMapping.get(this.url);
                    try {
                        Object object = Class.forName("httpServer.worker." + urlInto).newInstance();
                        Worker bworker = (Worker) object;
                        bworker.ctx=ctx;
                        bworker.url = this.url;
                        bworker.method=this.method;
                        bworker.session = UNID;
                        bworker.getContent = this.getContent;
                        bworker.postContent = postContent_str;
                        bworker.requestUnid=this.requestUnid;
                        bworker.Setup = Setup.getMapping();
                        bworker.headers=this.headers;
                        res = bworker.start();
                    } catch (ClassNotFoundException classNotE) {
                        System.out.println(classNotE.getMessage());
                        Log.errorLog("unid:" + this.requestUnid +" ["+urlInto+"]: is not find class. message: "+classNotE.getMessage());
                        res = this.url + ":class is not find!";
                    }
                } else {
                    Log.errorLog("unid:" + this.requestUnid +" ["+this.url+"]: is not find index.");
                    res = "the url  is not find!";
                }

                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
                response.headers().set("Content-Type", "text/html;charset=UTF_8");
                response.headers().set("Content-Length", response.content().readableBytes());
                response.headers().set("SERVER", "nettyServer");
                //请求的参数中没有session 发送session值
                if (!this.Cookie.containsKey("SESSIONID")) {
                    response.headers().set("Set-Cookie", "SESSIONID=" + UNID + ";path=/;");
                }
                ctx.writeAndFlush(response);
            }else{
                String[] url_array=this.url.split("\\.");
                int url_array_len=url_array.length;
                String url_end_state=url_array[url_array_len-1];
                if(url_end_state.equals("ico")){                           // 图标处理
                    String ImgFileUrl= Setup.getMapping().get("home")+"/views/image/favicon.ico";
                    ByteArrayOutputStream out= FaviconImg.readImgOut(ImgFileUrl);
                    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,Unpooled.wrappedBuffer(out.toByteArray()));
                    response.headers().set("Content-Type", "image/x-icon");
                    response.headers().set("Content-Length", response.content().readableBytes());
                    ctx.writeAndFlush(response);
                }else if(url_end_state.equalsIgnoreCase("jpeg") || url_array[1].equalsIgnoreCase("jpg") || url_array[1].equalsIgnoreCase("png")){                                                       //图片请求
                    String ImgFileUrl= Setup.getMapping().get("home")+this.url;
                    String ImgStyle=url_array[1].toLowerCase();
                    ByteArrayOutputStream out= ImageSend.readImgOut(ImgFileUrl,ImgStyle);
                    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,Unpooled.wrappedBuffer(out.toByteArray()));
                    response.headers().set("Content-Type", "image/"+ImgStyle);
                    response.headers().set("Content-Length", response.content().readableBytes());
                    ctx.writeAndFlush(response);
                }else if(url_end_state.equalsIgnoreCase("js")){         // js 处理
                    String ImgFileUrl= Setup.getMapping().get("home")+this.url;
                    String FileContent= JsSend.readJsOut(ImgFileUrl);
                    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,Unpooled.wrappedBuffer(FileContent.getBytes()));
                    response.headers().set("Content-Type", "application/x-javascript");
                    response.headers().set("Content-Length", response.content().readableBytes());
                    ctx.writeAndFlush(response);
                }else if(url_end_state.equalsIgnoreCase("css")){         //js加载
                    String ImgFileUrl=Setup.getMapping().get("home")+this.url;
                    String FileContent= JsSend.readJsOut(ImgFileUrl);
                    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,Unpooled.wrappedBuffer(FileContent.getBytes()));
                    response.headers().set("CONTENT-TYPE", "text/css");
                    response.headers().set("CONTENT-LENGTH", response.content().readableBytes());
                    ctx.writeAndFlush(response);
                }else{
                    res="未能识别路径";
                    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
                    response.headers().set("CONTENT-TYPE", "text/html;charset=utf-8");
                    response.headers().set("CONTENT-LENGTH", response.content().readableBytes());
                    response.headers().set("SERVER", "nettyServer");
                    ctx.writeAndFlush(response);
                    Log.errorLog("["+this.url+"]this url is error");
                }
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught:"+cause.getMessage());
    }
}