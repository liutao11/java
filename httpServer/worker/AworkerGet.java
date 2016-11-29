package httpServer.worker;



import httpServer.code.WebWorker;
import httpServer.code.Worker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.sql.*;
import com.mysql.jdbc.Driver;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;

/**
 * Created by lt on 2016/9/19.
 */
public class AworkerGet extends WebWorker  {
    public String execute() throws Exception{
        String object=this.GET.get("object");
        String server=this.GET.get("server");
        String gamedb=this.GET.get("gamedb");
        String logdb=this.GET.get("logdb");
        String returnString="false";
        if(object!=null&& server!=null &&gamedb!=null && logdb!=null) {
            try {
                object = URLDecoder.decode(object, "utf-8");
                server = URLDecoder.decode(server, "utf-8");
                String url=this.Setup.get("DBurl");
                String port=this.Setup.get("DBport");
                String user=this.Setup.get("DBuser");
                String password=this.Setup.get("DBpassword");
                String DBtable=this.Setup.get("DBtable");
                String dbConnectionUrl="jdbc:mysql://"+url+":"+port+"/"+DBtable+"?characterEncoding=utf8";
                try {
                    Connection mysqlConnection = DriverManager.getConnection(dbConnectionUrl, user, password);
                    String sql="select * from "+object+" where UNID='"+server+"'";
                    PreparedStatement mysql_result=mysqlConnection.prepareStatement(sql);
                    ResultSet mysql_re=mysql_result.executeQuery();
                    mysql_re.last();
                    int Row=mysql_re.getRow();
                    if(Row>0){
                        if(Row==1) {
                            mysql_re.first();
                            String NetUrl=mysql_re.getString("NetUrl");
                            String Port=mysql_re.getString("Port");
                            String UserName=mysql_re.getString("UserName");
                            String Password=mysql_re.getString("PassWd");
                            String PayDB=mysql_re.getString("PayDB");
                            String PayTable=mysql_re.getString("PayTable");
                            int TelnetPort=mysql_re.getInt("telnetPort");
                            if(!NetUrl.isEmpty() && !Port.isEmpty() && !UserName.isEmpty() && !Password.isEmpty() && !PayDB.isEmpty() && !PayTable.isEmpty()){
                                String mssqlurl="jdbc:sqlserver://"+NetUrl+":"+Port+";DataBaseName="+PayDB;
                                try {
                                    Connection mssqllink = DriverManager.getConnection(mssqlurl, UserName, Password);


                                    String gamedbState="1";
                                    try{
                                        String mssqlurlgame="jdbc:sqlserver://"+NetUrl+":"+Port+";DataBaseName="+gamedb;
                                        DriverManager.getConnection(mssqlurlgame, UserName, Password);
                                        gamedbState="0";
                                    }catch (SQLException e){
                                        gamedbState="1";
                                    }
                                    String logdbState="1";
                                    try{
                                        String mssqlurlLog="jdbc:sqlserver://"+NetUrl+":"+Port+";DataBaseName="+logdb;
                                        DriverManager.getConnection(mssqlurlLog, UserName, Password);
                                        logdbState="0";
                                    }catch (SQLException e){
                                        logdbState="1";
                                    }




                                    String testUserId="testUserId111";
                                    String testOrder="testOrder:test2342341"+System.currentTimeMillis();
                                    String insertSql="insert into "+PayDB+".dbo."+PayTable+" (UserID,sPayId,nNumber,createTimes,fromIdm,fromIds) values ('"+testUserId+"','"+testOrder+"','1000','2016-9-9 00:00:00',12,'{34213412}')";
                                    PreparedStatement mssqlPreparedStatement= mssqllink.prepareStatement(insertSql);
                                    int executeState=mssqlPreparedStatement.executeUpdate();
                                    if(executeState>0){
                                        String DeleteTestData="delete from "+PayDB+".dbo."+PayTable+" where UserID='"+testUserId+"'";
                                        int delInt=mssqllink.prepareStatement(DeleteTestData).executeUpdate();
                                        if(delInt>0){
                                            try {
                                                 Socket socket=new Socket(NetUrl,TelnetPort);
                                                //Socket socket=new Socket("172.16.3.161",TelnetPort);
                                                 PrintWriter printWriter= new PrintWriter(socket.getOutputStream());
                                                 printWriter.write("PAYGET ttee\\");
                                                 printWriter.flush();
                                                 //socket.shutdownOutput();
                                                 InputStreamReader inputStreamReader= new InputStreamReader(socket.getInputStream());
                                                 BufferedReader bufferedReader= new BufferedReader(inputStreamReader);
                                                 String read=null;
/*                                                 while ((read=bufferedReader.readLine())!=null){
                                                     System.out.println("服务器说:"+read);
                                                 }*/
                                                 bufferedReader.close();
                                                 inputStreamReader.close();
                                                 printWriter.close();
                                                 socket.close();
                                                 returnString =  "{\"state\":0,\"mysqlConnection\":0,\"serverLink\":0,\"serverSet\":0,\"gamedb\":0,\"insert\":0,\"del\":0,\"socket\":0,\"gamedbState\":"+gamedbState+",\"logdbState\":"+logdbState+"}";
                                            }catch (SocketException e){
                                                returnString =  "{\"state\":1,\"mysqlConnection\":0,\"serverLink\":0,\"serverSet\":0,\"gamedb\":0,\"insert\":0,\"del\":0,\"socket\":1,\"gamedbState\":"+gamedbState+",\"logdbState\":"+logdbState+"}";
                                            }
                                        }else {           //删除特殊数据失败
                                            returnString =  "{\"state\":1,\"mysqlConnection\":0,\"serverLink\":0,\"serverSet\":0,\"gamedb\":0,\"insert\":0,\"del\":1,\"gamedbState\":"+gamedbState+",\"logdbState\":"+logdbState+"}";
                                        }
                                    }else{                //插入游戏服失败。
                                        returnString =  "{\"state\":1,\"mysqlConnection\":0,\"serverLink\":0,\"serverSet\":0,\"gamedb\":0,\"insert\":1,\"gamedbState\":"+gamedbState+",\"logdbState\":"+logdbState+"}";
                                    }
                                }catch (SQLException e){            //游戏数据库连接失败
                                    returnString= "{\"state\":1,\"mysqlConnection\":0,\"serverLink\":0,\"serverSet\":0,\"gamedb\":1}";
                                }
                            }else{            //数据有空的字段
                                returnString= "{\"state\":1,\"mysqlConnection\":0,\"serverLink\":0,\"serverSet\":1}";
                            }
                        }else{                //指针重复
                            returnString= "{\"state\":1,\"mysqlConnection\":0,\"serverLink\":2}";
                        }
                    }else{                  //没有这个指针数据
                        returnString= "{\"state\":1,\"mysqlConnection\":0,\"serverLink\":1}";
                    }
                }catch (SQLException e){

                    returnString= "{\"state\":1,\"message\":\"mysql db is connection error\"}";
                }
            }catch (Exception e){

                returnString= "{\"state\":1,\"message\":\"args is error\"}";
            }
        }else{
            returnString = "{\"state\":0,\"message\":\"args is error\"}";
        }
        return  returnString;

    }
}
