package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import play.Logger;
import play.vfs.VirtualFile;

public class StringToFile {

	public static File makeFile(String sld, String sldName){
		File file = VirtualFile.fromRelativePath("slds/"+sldName+".xml").getRealFile();
		FileWriter fw;
		try {
			fw = new FileWriter(file);
		
		StringReader SReader = new StringReader(sld);
		BufferedReader reader = new BufferedReader(SReader);
		String line;
		BufferedWriter  bw = new BufferedWriter(fw); 
		while((line = reader.readLine()) != null){
			bw.write(line);
			bw.write("\n");
			
			Logger.info(line);
		}
		bw.close();
		fw.close();
		} catch (IOException e) {
			Logger.error("IO Error %s", e); 
		}
		return file;
	}
}
