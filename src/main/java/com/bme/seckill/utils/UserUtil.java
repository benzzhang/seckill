package com.bme.seckill.utils;

import com.bme.seckill.pojo.User;
import com.bme.seckill.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 批量生成用户并写入数据库，生成ticket保存到txt
public class UserUtil {
    private static void createUser(int count) throws SQLException, ClassNotFoundException, IOException {
        List<User> users = new ArrayList<>(count);
        for (int i=0; i < count; i++){
            User user = new User();
            user.setId(13000000000L+i);
            user.setNickname("user"+i);
            user.setSalt("1a2b3c4d");
            user.setPassword(MD5util.inputPassToDBPass("123456",user.getSalt()));
            user.setLoginCount(0);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("create user:");

        // JDBC插入数据库
       /* Connection conn = getConn();
        String sql = "insert into t_user(login_count,nickname,register_date,salt,password,id) values(?,?,?,?,?,?)";
        PreparedStatement psmt = conn.prepareStatement(sql);
        for (int i=0; i < users.size(); i++){
            User user = users.get(i);
            psmt.setInt(1,user.getLoginCount());
            psmt.setString(2,user.getNickname());
            psmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            psmt.setString(4,user.getSalt());
            psmt.setString(5,user.getPassword());
            psmt.setLong(6,user.getId());
            psmt.addBatch();
        }
        psmt.executeBatch();
        psmt.clearParameters();
        conn.close();
        System.out.println("insert into DB:");
*/
        // 登录，生成UserTiket
        String url = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\Administrator\\Desktop\\config.txt");
        if (file.exists()){
            file.delete();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file,"rw");
        randomAccessFile.seek(0);

        for (int i = 0; i < users.size(); i++){
            User user = users.get(i);
            URL urll = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urll.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream out = httpURLConnection.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5util.inputPassToFormPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = httpURLConnection.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buff)) >= 0){
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = ((String) respBean.getObject());
            System.out.println("create userTicket:"+user.getId());

            String row = user.getId() + "," + userTicket;
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(row.getBytes());
            randomAccessFile.write("\r\n".getBytes());
            System.out.println("write to file:"+user.getId());
        }
        randomAccessFile.close();

        System.out.println("over");
    }

    private static Connection getConn() throws ClassNotFoundException, SQLException {
         String url = "jdbc:mysql://localhost:3306/seckill?serverTimezone=UTC";
         String username = "root";
         String password = "cqmu0000";
         String driver = "com.mysql.cj.jdbc.Driver";
         Class.forName(driver);
         return DriverManager.getConnection(url,username,password);
    }

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        createUser(500);
    }
}
