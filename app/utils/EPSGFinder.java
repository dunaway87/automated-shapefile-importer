package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Query;

import org.dom4j.Branch;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.vfs.VirtualFile;

import com.bbn.openmap.layer.shape.ESRIBoundingBox;
import com.bbn.openmap.layer.shape.ESRIPoint;
import com.bbn.openmap.layer.shape.ESRIRecord;
import com.bbn.openmap.layer.shape.ShapeFile;
import com.bbn.openmap.proj.Proj;
import com.bbn.openmap.proj.ProjectionFactory;
import com.bbn.openmap.proj.ProjectionLoader;
import com.bbn.openmap.proj.coords.CoordinateReferenceSystem;
import com.bbn.openmap.proj.coords.GeoCoordTransformation;

public class EPSGFinder {

	public static void doItToIt() throws IOException{
		JPA.em().createNativeQuery("Delete from epsg_lookup").executeUpdate();
		File file = VirtualFile.fromRelativePath("conf/epsgs").getRealFile();
		FileReader reader = new FileReader(file);
		BufferedReader br = new BufferedReader(reader);
		String line;
		while((line = br.readLine()) != null){
			if(line.contains("INSERT")){
				JPA.em().createNativeQuery(line).executeUpdate();
			}
		}
		br.close();
		List<Object[]>  list = JPA.em().createNativeQuery("Select * from epsg_lookup").getResultList();
		int count = 0;
		String URL = "jdbc:postgresql://"+Play.configuration.getProperty("ipaddress")+"/"+Play.configuration.getProperty("database")+"?user="+Play.configuration.getProperty("user")+"&password="+Play.configuration.getProperty("password");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL);
		} catch (Exception e){
		}
		JPA.em().flush();
		JPA.em().getTransaction().commit();
		JPA.em().getTransaction().begin();
		for(int i = 0; i< list.size(); i++){
			try{
				String authName = (String)list.get(i)[1];
				if(authName.contains("RI")){
					String qur = "insert into spatial_ref_sys values ("+list.get(i)[0]+",'"+list.get(i)[1]+"',"+list.get(i)[2]+",'"+list.get(i)[3]+"','"+list.get(i)[4]+"')";
					Logger.info(qur);
					PreparedStatement pstmt = conn.prepareStatement(qur);
					Logger.info("worked?  %s",pstmt.executeUpdate());
				}
			} catch(Exception e){
				Logger.error("%s", e);
				count++;
			}
		}

		Logger.info("worked %s  didn't work %s", list.size()-count, count);










	}
	public static void updateEPSG(String uuid, int epsg, String shapetype){
		String URL = "jdbc:postgresql://"+Play.configuration.getProperty("ipaddress")+"/"+Play.configuration.getProperty("database")+"?user="+Play.configuration.getProperty("username")+"&password="+Play.configuration.getProperty("password");
		Logger.info("connecting to %s for epsg stuff",Play.configuration.getProperty("ipaddress"));
		Connection conn = null;
		String geometryColumn = "";
		try {
			conn = DriverManager.getConnection(URL);
		} catch (Exception e){
		}	
		try {
			Logger.info("select f_geometry_column from geometry_columns where f_table_name ='"+uuid+"'");
			//ResultSet rs = conn.prepareStatement("select f_geometry_column from geometry_columns where f_table_name ='"+uuid+"'").executeQuery();
			//while (rs.next()){
			//	geometryColumn = rs.getString(1);
			//}
			geometryColumn = "the_geom";
			Logger.info("geometry column  %s", geometryColumn);
			Logger.info("Select AddGeometrycolumn('"+uuid+"', 'my_geom', 4326,'"+shapetype+"', 2)");
			conn.prepareStatement("Select AddGeometrycolumn('"+uuid+"', 'my_geom', 4326,'"+shapetype+"', 2)").executeQuery();
			
			
			//Logger.info("select UpdateGeometrySRID('public', '"+uuid+"', '"+geometryColumn+"', "+epsg+")");
		//	conn.prepareStatement("select UpdateGeometrySRID('public', '"+uuid+"', '"+geometryColumn+"', "+epsg+")").execute();
			
			Logger.info("UPDATE "+uuid+" set "+geometryColumn+" = ST_SETSRID("+geometryColumn+", "+ epsg+")");
			conn.prepareStatement("UPDATE "+uuid+" set "+geometryColumn+" = ST_SETSRID("+geometryColumn+", "+ epsg+")").execute();
			
			Logger.info("Update geometry_columns set srid = "+epsg+ " where f_table_name ='"+uuid+"' and f_geometry_column = '"+geometryColumn+"'");
			conn.prepareStatement("Update geometry_columns set srid = "+epsg+ " where f_table_name ='"+uuid+"' and f_geometry_column = '"+geometryColumn+"'").execute();
			Logger.info("Update "+uuid+" set my_geom = st_transform("+geometryColumn+", 4326)");
			conn.prepareStatement("Update "+uuid+" set my_geom = st_transform("+geometryColumn+", 4326)").execute();
			Logger.info("ALTER TABLE "+ uuid+" DROP COLUMN "+ geometryColumn);
			conn.prepareStatement("ALTER TABLE "+ uuid+" DROP COLUMN "+ geometryColumn).execute();
		} catch (SQLException e) {
			Logger.error("%s",e);
		}

	}
	public static int getEPSG(String crs){
		Logger.info("%s", "Select srid from epsg_lookup where srtext like '"+crs+"'");
		List<Integer> list = JPA.em().createNativeQuery("Select srid from epsg_lookup where srtext like '%"+crs+"%'").getResultList();	
		Logger.info("%s",list);
		if(list.size() != 0){
			return list.get(0);
		} else {
			return -1;
		}

	}

	public static boolean needsImport(){
		Number number = (Number) JPA.em().createNativeQuery("Select count(*) from epsg_lookup").getSingleResult();
		int count = number.intValue();
		if (count == 0){
			return true;
		} else{
			return false;
		}
	}
}
