package geoserver;

import java.io.File;

import play.Logger;
import play.Play;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;

public class GeoserverTools {
	

	public static final String RESTURL  =Play.configuration.getProperty("RESTURL");
	public static final String RESTUSER = Play.configuration.getProperty("RESTUSER");
	public static final String RESTPW   = Play.configuration.getProperty("RESTPW");
	
	
	public static void importLayer(String layerName, int epsg, String style){
		Logger.info("using style %s",style.replace(".xml", ""));
		Logger.info("tableName    %s", layerName);
		Logger.info("epsg %s", epsg);
        GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
        publisher.publishDBLayer(Play.configuration.getProperty("workspace"), Play.configuration.getProperty("store"), layerName, "EPSG:"+epsg, style.replace(".xml", ""));
               
	}
	public static boolean publishSLD(File file, String tableName){
		
	    GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
	    GeoServerRESTReader reader;
	   
	    boolean published = publisher.publishStyle(file, tableName);
	    
	    Logger.info("pbulished sld ?    %s", published);
	    
	    return published;
	}


	
	public static void updateStyle(String tableID, String styleID){
		GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
		GSLayerEncoder le = new GSLayerEncoder();

		le.setDefaultStyle(styleID);
		publisher.configureLayer("Test", tableID, le);
	}
}
