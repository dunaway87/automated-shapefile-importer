package jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import javax.persistence.Query;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.vfs.VirtualFile;

public class EPSGAdder extends Job{

	public void doJob(){
		try {
			addEPSGs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void addEPSGs() throws IOException{
		JPA.em().createNativeQuery("Delete from epsg_lookup").executeUpdate();
		File file = VirtualFile.fromRelativePath("conf/epsgs").getRealFile();
		FileReader reader;
		
			reader = new FileReader(file);
		
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
		Logger.info("number of rows in looup table ?    %s", list.size());
		String URL = "jdbc:postgresql://"+Play.configuration.getProperty("ipaddress")+"/"+Play.configuration.getProperty("database")+"?user="+Play.configuration.getProperty("username")+"&password="+Play.configuration.getProperty("password");
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
	

}
