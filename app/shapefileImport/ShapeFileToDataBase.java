package shapefileImport;

import models.ShapeType;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.ShapeFileInfo;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import utils.EPSGFinder;
import utils.Timer;

public class ShapeFileToDataBase {


	public static ShapeFileInfo loadShapefileIntoPostGIS(URL shapefileURL, String postgisTableName, String uuid) {
		ShapeFileInfo info = new ShapeFileInfo();
		String geomType = null;
		int epsg = 0;
		boolean badEPSG = false;
		String atShapeType = "";
		String epsgType = "user-defined";
		try {
			Logger.info("postgisTableName   %s", postgisTableName);
			Map<Object,Serializable> shapeParams = new HashMap<Object,Serializable>();
			shapeParams.put("url", shapefileURL);
			DataStore shapeDataStore = DataStoreFinder.getDataStore(shapeParams);
			String typeName = shapeDataStore.getTypeNames()[0];
			FeatureSource<SimpleFeatureType,SimpleFeature> featSource =  shapeDataStore.getFeatureSource(typeName);
			FeatureCollection<SimpleFeatureType,SimpleFeature>
			featSrcCollection = featSource.getFeatures();
			SimpleFeatureType ft = shapeDataStore.getSchema(typeName);



			// feature type copy to set the new name
			SimpleFeatureTypeBuilder builder = new  SimpleFeatureTypeBuilder();
			builder.setName(uuid);
			builder.setAttributes(ft.getAttributeDescriptors());
			builder.setCRS(ft.getCoordinateReferenceSystem());

			SimpleFeatureType newSchema = builder.buildFeatureType();



			// management of the projection system
			CoordinateReferenceSystem crs = ft.getCoordinateReferenceSystem();
			// test of the CRS based on the .prj file
			Integer crsCode = null;
			try {
				crsCode = CRS.lookupEpsgCode(crs, true);
			} catch (Exception e) {
				Logger.error("could not find crs %s", e);
				
			}
			if (crsCode != null){
				epsg = crsCode;
			} 
			
			if(epsg == 0 && crs != null){
				Logger.info("%s",crs.getName());
				epsg = EPSGFinder.getEPSG(crs.getName().toString());
				badEPSG = true;
				epsgType = "corrected";
			} else if(crs == null){
				epsg = 4326;
				epsgType =  "forced";
			}

			geomType = ft.getGeometryDescriptor().getType().getName().toString();
			JsonArray array = new JsonArray();
			List<AttributeType> list = ft.getTypes();
			for (int i = 0; i < list.size(); i++){

				AttributeType at = list.get(i);
				
				String atStr = at.toString();

				String name = at.getName().toString();
				
				String valueType = "unknown";
				if ( atStr.contains("Integer")){
					valueType = "int";
				} else if (atStr.contains("Line") || atStr.contains("Polygon") || atStr.contains("Polygon")){
					atShapeType = at.getName().toString().toUpperCase();
					valueType = "geometry";
				} else if (atStr.contains("Double")){
					valueType = "double";
				} else if (atStr.contains("String")){
					valueType = "String";
				}
				JsonObject obj = new JsonObject();
				obj.addProperty("columnName", name);
				obj.addProperty("valueType", valueType);
				array.add(obj);
			}
			
			info.columnsArray = array.toString();


			Map postGISparams = new HashMap();

			postGISparams.put("dbtype", "postgis");
			postGISparams.put("host", Play.configuration.getProperty("ipaddress"));
			postGISparams.put("port", new Integer(5432));
			postGISparams.put("database", Play.configuration.getProperty("database"));
			postGISparams.put("user", Play.configuration.getProperty("username"));
			postGISparams.put("passwd", Play.configuration.getProperty("password"));

			Logger.info("here1");

			DataStore dataStore = DataStoreFinder.getDataStore(postGISparams);
			Logger.info("here2");

			dataStore.createSchema(newSchema);
			Logger.info("here3");

			FeatureStore<SimpleFeatureType,SimpleFeature> featStore = (FeatureStore<SimpleFeatureType,SimpleFeature>)dataStore.getFeatureSource(uuid);
			Logger.info("here4");
			featStore.addFeatures(featSrcCollection);
		} catch (IOException e) {
			Logger.error("error in shapefile import   %s", e);
		}

		ShapeType shapeType = getShapeType(geomType);
		info.shapeType =  shapeType.toString();
		if(badEPSG == true){
			 EPSGFinder.updateEPSG(uuid,epsg, atShapeType);
			 epsg = 4326;
		}
		info.epsg = epsg;
		info.epsgType = epsgType;
		Logger.info("EPSG:  %s", epsg);
		info.original_layer_name = postgisTableName;
		info.uuid = uuid;
		info.save();
		return info;
	}
	public static ShapeType getShapeType(String geomType){
		ShapeType shape;

		if(geomType.contains("Line")){
			shape = ShapeType.LINE;
		} else if (geomType.contains("Polygon")) {
			shape = ShapeType.POLYGON;
		} else {
			shape = ShapeType.POINT;
		}
		return shape;
	}


}
