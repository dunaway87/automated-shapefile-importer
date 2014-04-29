package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import play.Logger;
import play.vfs.VirtualFile;

public class FileUnzipper {

	public static File unzip(File zipFileAsFile, String timestamp) throws ZipException, IOException{
		
		
		ZipFile zipFile = new ZipFile(zipFileAsFile);
		File destinationFolder =  VirtualFile.fromRelativePath(timestamp).getRealFile();
		destinationFolder.mkdir();
		File file = FileUnzipper.unzipFileIntoDirectory(zipFile, destinationFolder, timestamp);
		return file;
	}

	
	  public static File unzipFileIntoDirectory(ZipFile zipFile, File destination, String timestamp) {
		    Enumeration files = zipFile.entries();
		    File f = null;
		    FileOutputStream fos = null;
		    
		    while (files.hasMoreElements()) {
		      try {
		        ZipEntry entry = (ZipEntry) files.nextElement();
		        InputStream eis = zipFile.getInputStream(entry);
		        byte[] buffer = new byte[1024];
		        int bytesRead = 0;
		  
		        f = new File(destination.getAbsolutePath() + File.separator + entry.getName());
		        
		        if (entry.isDirectory()) {
		          f.mkdirs();
		          continue;
		        } else {
		          f.getParentFile().mkdirs();
		          f.createNewFile();
		        }
		        
		        fos = new FileOutputStream(f);
		  
		        while ((bytesRead = eis.read(buffer)) != -1) {
		          fos.write(buffer, 0, bytesRead);
		        }
		      } catch (IOException e) {
		        e.printStackTrace();
		        continue;
		      } finally {
		        if (fos != null) {
		          try {
		            fos.close();
		          } catch (IOException e) {
		            // ignore
		          }
		        }
		      }
		    }
		    
		    String[] array = destination.list();
		    String shapeFilePath = null;
		    for (int i = 0; i < array.length; i++) {
				String fileName = array[i];
				if(fileName.endsWith(".shp")){
					shapeFilePath = fileName;
				}
		    }
		    File shapeFile = new File(destination+"/"+shapeFilePath);
			return shapeFile;
		  }
	
	
	
	

}
