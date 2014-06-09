package graphene.ingest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import com.jolbox.bonecp.BoneCP;

public class ExampleJDBCInjected {
	private static Registry registry;
	private static BoneCP cp;

	public static void setup() {

		RegistryBuilder builder = new RegistryBuilder();
		
		registry = builder.build();
		registry.performRegistryStartup();
		cp = registry.getService(BoneCP.class);

	}

	public static void main(String[] args) throws Exception {
		setup();
		Connection connection = cp.getConnection(); // fetch a connection
		
		if (connection != null){
			System.out.println("Connection successful!");
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT 1"); // do something with the connection.
			while(rs.next()){
				System.out.println(rs.getString(1)); // should print out "1"'
			}
		}
	}
}
