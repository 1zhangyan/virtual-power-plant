import java.sql.*;

class Test {
public static void main(String[] args) {
// 此处填写Lindorm时序引擎JDBC连接地址
String url = "jdbc:lindorm:tsdb:url=http://ld-bp12pt80qr38p****-proxy-tsdb-pub.lindorm.rds.aliyuncs.com:8242";
Connection conn = null;

        try {
            conn = DriverManager.getConnection(url);
            try (Statement stmt = conn.createStatement()) {
                //创建时序数据表，默认访问 default database
                stmt.execute("CREATE TABLE sensor1 (device_id VARCHAR TAG,region VARCHAR TAG,time TIMESTAMP,temperature DOUBLE,humidity DOUBLE,PRIMARY KEY(device_id))");

                //批量写入数据
                stmt.addBatch("INSERT INTO sensor1(device_id, region, time, temperature, humidity) values('F07A1260','north-cn','2021-04-22 15:33:00',12.1,45)");
                stmt.addBatch("INSERT INTO sensor1(device_id, region, time, temperature, humidity) values('F07A1260','north-cn','2021-04-22 15:33:10',13.2,47)");
                stmt.addBatch("INSERT INTO sensor1(device_id, region, time, temperature, humidity) values('F07A1260','north-cn','2021-04-22 15:33:20',10.6,46)");
                stmt.addBatch("INSERT INTO sensor1(device_id, region, time, temperature, humidity) values('F07A1261','south-cn','2021-04-22 15:33:00',18.1,44)");
                stmt.addBatch("INSERT INTO sensor1(device_id, region, time, temperature, humidity) values('F07A1261','south-cn','2021-04-22 15:33:10',19.7,44)");
                stmt.executeBatch();
                stmt.clearBatch();
            }

            // 使用绑定参数的方式查询数据
            // 强烈建议指定时间范围减少数据扫描
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT device_id, region,time,temperature,humidity FROM sensor1 WHERE time >= ? and time <= ?")) {
                Timestamp startTime =Timestamp.valueOf("2021-04-22 15:33:00");
                Timestamp endTime = Timestamp.valueOf("2021-04-22 15:33:20");
                pstmt.setTimestamp(1, startTime);
                pstmt.setTimestamp(2, endTime);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String device_id = rs.getString("device_id");
                        String region = rs.getString("region");
                        Timestamp time = rs.getTimestamp("time");
                        Double temperature = rs.getDouble("temperature");
                        Double humidity = rs.getDouble("humidity");
                        System.out.printf("%s %s %s %f %f\n", device_id, region, time, temperature, humidity);
                    }
                }
            }
        } catch (SQLException e) {
            // 异常处理需要结合实际业务逻辑编写
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}