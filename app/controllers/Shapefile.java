package controllers;

import play.*;
import play.db.jpa.JPA;
import play.libs.F.Promise;
import play.libs.WS;
import play.mvc.*;
import play.vfs.VirtualFile;
import services.MongoFile;
import shapefileImport.ShapeFileToDataBase;
import utils.FileUnzipper;
import utils.FolderContense;
import utils.StringToFile;
import utils.Timer;


import geoserver.GeoserverTools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipException;

import jobs.InitStyleColors;
import jobs.ShapefileImport;


import MyTestsNotForRossEyes.GetUUIDS;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import models.*;

public class Shapefile extends Controller {

	public static void importSF(String callback) throws InterruptedException, ExecutionException, ZipException, IOException{
		String uuid = params.get("tableName", String.class);
		File zipFileAsFile =  params.get("zipFile", File.class);
		String time =  Long.toString(Calendar.getInstance().getTimeInMillis());
		File file  = FileUnzipper.unzip(zipFileAsFile, time);
		ShapefileImport sfi = new ShapefileImport(file, uuid);
		sfi.now().get();
	}

}