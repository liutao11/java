package httpServer.classFile;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by lt on 2016/11/7.
 */
public class MySQLDBPool {
    private final int POOL_SIZE_NUM=10;
    private ArrayBlockingQueue<Connection> cons=new ArrayBlockingQueue<Connection>(POOL_SIZE_NUM);


    public MySQLDBPool() throws ClassNotFoundException,SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        for (int i=1;i<=10;i++){
            Connection con=DriverManager.getConnection("jdbc:mysql://172.16.3.180/test?characterEncoding=utf8&useSSL=true","broot","123456");
            this.cons.add(con);
        }
        //启动一个线程维护链接心疼
        Timer mysqlHeartbeat=new Timer();
        mysqlHeartbeat.schedule(new mysqlHeartbeatTask(this.cons),0,20000);
    }
    public Connection getDBlinkOne() throws ClassNotFoundException,SQLException{                   //得到一个数据库链接
        Connection reCon=this.cons.poll();
        if(reCon==null){
            Class.forName("com.mysql.jdbc.Driver");
            reCon=DriverManager.getConnection("jdbc:mysql://172.16.3.180/test?characterEncoding=utf8&useSSL=true","broot","123456");
        }

        System.out.println(reCon);
        System.out.println(this.cons.size());
        return reCon;
    }
    public void  backDBlink(Connection con){
        this.cons.offer(con);
    }
    public  static  void  main(String[] a){
        try {
            MySQLDBPool mysqDBPool=new MySQLDBPool();
            Connection ss=mysqDBPool.getDBlinkOne();
            Statement st=ss.createStatement();
            ResultSet resultSet=st.executeQuery("show tables");
            while (resultSet.next()){
                 System.out.println(resultSet.getString(1));
            }
            st.close();
            Thread.sleep(6000);
            mysqDBPool.backDBlink(ss);
        }catch (ClassNotFoundException c){
           System.out.println(c.getMessage());
        }catch (SQLException s){
            System.out.println(s.getMessage());
        }catch (InterruptedException ie){
            System.out.println(ie.getMessage());
        }
    }


}
class  mysqlHeartbeatTask extends  TimerTask{
    private ArrayBlockingQueue<Connection> cons;
    public mysqlHeartbeatTask(ArrayBlockingQueue<Connection> cons){
        this.cons=cons;
    }
    @Override
    public  void  run(){
        try {
            for (Connection con : this.cons) {
                Statement st = con.createStatement();
                st.executeQuery("select @@version");
                st.close();
            }
        }catch (SQLException SqlE){

        }
    }
}

