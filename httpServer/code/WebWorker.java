package httpServer.code;

import httpServer.classFile.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by lt on 2016/10/28.
 */
public abstract class WebWorker extends Worker {
    public String start() throws Exception {
        String showStr="GetWorker is default";
        try {
            this.Init();                   //初始化
            showStr=this.execute();        //开始运行逻辑方法
        }catch (UnsupportedEncodingException StringEncodingE){
            Log.errorLog("InitWorker_Encoding :: "+StringEncodingE.getMessage());
        }
        return  showStr;
    }
    public void Init() throws UnsupportedEncodingException {
        this.home=this.Setup.get("home");
        //处理get内容

        if(this.getContent!=null){
            if(this.getContent.indexOf("&")>0){
                String[] getString_a = this.getContent.split("&");
                for (String getString_a_v : getString_a) {
                    if(getString_a_v .indexOf("=")>0) {
                        String[] getString_a_v_a = getString_a_v.split("=");
                        if (getString_a_v_a.length > 1) {
                            this.GET.put(getString_a_v_a[0], URLDecoder.decode(getString_a_v_a[1], "utf-8"));         //url解码
                        } else {
                            this.GET.put(getString_a_v_a[0], null);
                        }
                    }else{
                        this.GET.put(getString_a_v, null);
                    }
                }
            }else{
                if(this.getContent.indexOf("=")>0) {
                    String[] getString_a_v_a = this.getContent.split("=");
                    if (getString_a_v_a.length > 1) {
                        this.GET.put(getString_a_v_a[0], URLDecoder.decode(getString_a_v_a[1], "utf-8"));         //url解码
                    } else {
                        this.GET.put(getString_a_v_a[0], null);
                    }
                }else{
                    this.GET.put(this.getContent,null);
                }
            }
        }

        //处理post内容
        if(this.method.equalsIgnoreCase("post")) {
            if (this.postContent != null) {
                if (this.postContent.indexOf("&") > 0) {
                    String[] postString_a = this.postContent.split("&");
                    for (String postString_a_v : postString_a) {
                        if (postString_a_v.indexOf("=") > 0) {
                            String[] postString_a_v_a = postString_a_v.split("=");
                            if (postString_a_v_a.length > 1) {
                                this.POST.put(postString_a_v_a[0], URLDecoder.decode(postString_a_v_a[1], "utf-8"));         //url解码
                            } else {
                                this.POST.put(postString_a_v_a[0], null);
                            }
                        } else {
                            this.POST.put(postString_a_v, null);
                        }
                    }
                }else{
                    if(this.postContent.indexOf("=")>0) {
                        String[] getString_a_v_a = this.postContent.split("=");
                        if (getString_a_v_a.length > 1) {
                            this.POST.put(getString_a_v_a[0], URLDecoder.decode(getString_a_v_a[1], "utf-8"));         //url解码
                        } else {
                            this.POST.put(getString_a_v_a[0], null);
                        }
                    }else{
                        this.POST.put(this.getContent,null);
                    }
                }
            }
        }
    }
    public abstract String execute() throws Exception;
}
