import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DBWriter {

	public static void main(String[] args) throws Exception {

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/temp", "root", "password");
		conn.setAutoCommit(false);
		
        long start = System.nanoTime();
        int numMessages = 1000;
		for(int i = 0; i < numMessages; i++){
			PreparedStatement stmt = conn.prepareStatement("insert into commslog values (?, ?)");
			stmt.setString(1, "" + System.currentTimeMillis());
	
			// 1000 chars
			stmt.setString(2, "some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, some really long string, ");
	
			stmt.executeUpdate();

			//commit and close
			stmt.close();
			conn.commit();
        }
        
        System.out.println("avg write " + (((System.nanoTime()-start)/1000000.0)/numMessages) + " ms");
		
		conn.close();
	}
}
