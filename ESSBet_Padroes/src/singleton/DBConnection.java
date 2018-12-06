package singleton;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class DBConnection {
	private static DBConnection instance;
	
	private DBConnection () {
	}
	
	public static DBConnection getInstance() {
		if (instance == null) {
            instance = new DBConnection();
        }
		return instance;
	}
	
	public MongoClient getConnection() {
		return MongoClients.create();
	}
	
}
