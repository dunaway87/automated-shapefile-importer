package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import play.Logger;
import play.Play;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.gridfs.GridFS;

public class MongoDB {

	public static final String DB_NAME = "oceanWorkspace";

	private static Mongo mongo = null;
	private static GridFS fs = null;
	
	
	public static final DB getDB() {
		return getMongo().getDB(DB_NAME);
	}
	
	public static final GridFS getGridFS() {
		
		if (fs == null) {
			try {
				fs = new GridFS(getDB());
				
			} catch (Exception e) {
				Logger.error("unable to instantiate GridFS instance %s", e);
			}
		}

		return fs;
	}
	

	
	
	public static final Mongo getMongo() {
		if (mongo == null) {
			try {

				MongoOptions options = new MongoOptions();
				options.autoConnectRetry = true;
				options.connectTimeout = 5000; // 5 seconds
				options.socketTimeout = 60000; // 60 seconds;
				System.out.println("here");
				String property = "192.168.8.21,192.168.8.22,192.168.8.23";
				System.out.println("here2");
				Logger.info("%s", property);
				String[] endpoints = property.split(",");

				Logger.info("creating MongoDB connection to %s", Arrays.toString(endpoints));
				
				List<ServerAddress> addresses = new ArrayList<ServerAddress>();
				for (String endpoint : endpoints) {
					addresses.add(new ServerAddress(endpoint));
				}
				
				mongo = new Mongo(addresses, options);
				mongo.setWriteConcern(WriteConcern.FSYNC_SAFE);

			} catch (Exception e) {
				Logger.error("unable to instantiate Mongo instance %s", e);
			}
		}

		return mongo;
	}

}