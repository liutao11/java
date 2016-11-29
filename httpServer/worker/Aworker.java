package httpServer.worker;

import httpServer.classFile.MSmarty;
import httpServer.code.WebWorker;
import com.mysql.jdbc.Driver;

import java.awt.*;
import java.sql.*;


/**
 * Created by lt on 2016/8/31.
 */
public class Aworker extends WebWorker {
    public String execute() throws Exception{
        MSmarty mSmarty=new MSmarty("./httpServer/views/Aworker.html");
        String url=this.Setup.get("DBurl");
        String port=this.Setup.get("DBport");
        String user=this.Setup.get("DBuser");
        String password=this.Setup.get("DBpassword");
        String DBtable=this.Setup.get("DBtable");
        String dbConnectionUrl="jdbc:mysql://"+url+":"+port+"/"+DBtable+"?characterEncoding=utf8";

        String objectShow="";
        try {
            Connection mConnection=DriverManager.getConnection(dbConnectionUrl,user,password);
            String sql="show tables";
            PreparedStatement pStat= mConnection.prepareStatement(sql);
            ResultSet resultSet=pStat.executeQuery();
            while (resultSet.next()){
                String tableName=resultSet.getString(1);
                int indexOf=tableName.indexOf("PayServerLink");
                if(indexOf>=0){
                    objectShow+="<div class='objectName'>"+tableName+"</div>";
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        mSmarty.assign("objectShow",objectShow);

        mSmarty.assign("AName","充值系统测试页面");
        return mSmarty.display();
    }
}