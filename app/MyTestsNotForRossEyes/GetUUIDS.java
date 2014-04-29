package MyTestsNotForRossEyes;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import play.Logger;
import play.libs.WS;
import play.mvc.Scope.Params;
import services.MongoFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;


public class GetUUIDS {

	/*
	public static void uploadAllShapefiles() throws SQLException{
		 JsonArray array = getShapefileNames();
	       Logger.info("%s", array);
	    	
	    	for (int i = 0; i < array.size(); i++) {
				JsonObject obj = array.get(i).getAsJsonObject();
				String layer = obj.get("layer").getAsString();
				String uuids = obj.get("uuids").getAsString();
				//Logger.info("layer  %s", layer);
				//Logger.info("uuids  %s", uuids);
				System.out.println("localhost:9000/importFromMongo?json="+uuids+"&uuid="+layer);

				//WS.url("http://staging1.axiom:9009/importFromMongo?json="+uuids+"&uuid="+layer).timeout("5min").get();
	    	}
	    	
	}
*/
		


	
	public static void main(String[] args) throws SQLException {
		//old();
//		/uploadAllShapefiles();
		getSingleShapefile("image");
	}
	public static void getSingleShapefile(String partialFileName) throws SQLException{


		String URL = "jdbc:postgresql://oltp1.axiom/oceanWorkspace?user=postgres&password=Axiomrox5";


		Connection conn = DriverManager.getConnection(URL);
		PreparedStatement pstmt =conn.prepareStatement("Select uuid from document where mimetype like '%"+partialFileName+"%'");
		System.out.println(pstmt.toString());
		ResultSet results = pstmt.executeQuery();
		//LinkedList<String> list = new LinkedList();
		while(results.next()){
			//list.add(results.getString(1));
			downloadMongoFile(results.getString(1));
		}
		
		
		
		
	}

	public static void downloadMongoFile(String uuid){
		File file = new File("/projects/shapefile-importer/app/images/");
		try {
			 MongoFile.get(uuid, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*	public static JsonArray getShapefileNames() throws SQLException{
		String URL = "jdbc:postgresql://192.168.1.225/oceanWorkspace?user=postgres&password=postgres";

		Connection conn = DriverManager.getConnection(URL);
		PreparedStatement pstmt =conn.prepareStatement("Select distinct filename from document where filename like '%shp'");
		ResultSet results = pstmt.executeQuery();
		JsonArray allFiles = new JsonArray();
		while(results.next()){
			//list.add(results.getString(1));
			PreparedStatement pstmt2 = conn.prepareStatement("Select uuid from document where filename like '"+results.getString(1).replace(".shp", "")+"%'");
			ResultSet results2 = pstmt2.executeQuery();
			LinkedList list = new LinkedList();
			while(results2.next()){
				list.add(results2.getString(1));
			}
			JsonObject shapefile = new JsonObject();
			shapefile.addProperty("layer", results.getString(1).replace(".shp", ""));
			shapefile.addProperty("uuids", list.toString());
			allFiles.add(shapefile);
			
		}
		return allFiles;
	
		

	
	}
*/
















}
