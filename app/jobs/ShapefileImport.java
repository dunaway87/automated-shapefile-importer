package jobs;

import geoserver.GeoserverTools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;
import java.util.zip.ZipException;

import models.ShapeFileInfo;

import org.opengis.referencing.FactoryException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import play.Logger;
import play.jobs.Job;
import shapefileImport.ShapeFileToDataBase;
import utils.FileUnzipper;
import utils.Timer;

public class ShapefileImport extends Job{
	private File shapefile;
	private String uuid;
	public ShapefileImport(File file, String uuid){
		this.shapefile = file;
		this.uuid = uuid;
	}
	
	public JsonObject doJobWithResult(){
		return importShapefile(shapefile,uuid);
	}

	
	
	public static JsonObject importShapefile(File shapefile, String uuid){
	//	UUID uuid = UUID.randomUUID();
		Timer timer = new Timer();
		ShapeFileInfo shapeFileInfo = new ShapeFileInfo();
		String layerName = "error";
		try {
			
			layerName = shapefile.getName().replace(".shp", "");
			layerName = layerName.substring(layerName.indexOf("\\")+1);
			URL fileUrl = shapefile.toURL();
			Logger.info("fileUrl   %s,    layerName  %s, uuid     %s", fileUrl, layerName, uuid);
			shapeFileInfo = ShapeFileToDataBase.loadShapefileIntoPostGIS(fileUrl, layerName, uuid);
			
		}catch (IOException e) {
			Logger.error("IOException %s", e);
		}

		JsonObject layerinfo = new JsonObject();

		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(shapeFileInfo.columnsArray).getAsJsonArray();

		GeoserverTools importer = new GeoserverTools();
		importer.importLayer(uuid.toString(), shapeFileInfo.epsg, shapeFileInfo.shapeType.toLowerCase());
		

		layerinfo.add("columns", array);
		layerinfo.addProperty("style", shapeFileInfo.shapeType.toLowerCase());
		layerinfo.addProperty("shapeType", shapeFileInfo.shapeType);
		layerinfo.addProperty("buildTime", timer.elapsed());
		layerinfo.addProperty("epsg", shapeFileInfo.epsg);
		layerinfo.addProperty("layerName", layerName);
		layerinfo.addProperty("uuid", uuid.toString());
		return(layerinfo);
	}

}
