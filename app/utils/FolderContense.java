package utils;

import java.io.File;

import play.Logger;
import play.vfs.VirtualFile;

public class FolderContense {

	public static void deleteContense(File file){
		String[] array = file.list();
		for (int i = 0; i < array.length; i++) {
			File nestedFile = new File(file.getAbsolutePath()+File.separator+array[i]);
			Logger.info("deleting file %s, success  %s",array[i],nestedFile.delete());
		}
		file.delete();

	}
}
