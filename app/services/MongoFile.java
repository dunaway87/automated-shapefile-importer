package services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import org.bson.types.ObjectId;

import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

import play.vfs.VirtualFile;

public class MongoFile {
	
	public static String get(String uuid, File folder) throws IOException{
		Mongo m =MongoDB.getMongo();
		GridFS fs = new GridFS(m.getDB("oceanWorkspace"));

		ObjectId objectId = new ObjectId(uuid);
		GridFSDBFile find = fs.find(objectId);

		InputStream inputStream = find.getInputStream();

		String fileName = find.getFilename();
		// write the inputStream to a FileOutputStream
		File f = new File(folder.getAbsolutePath()+File.separator + fileName);
	
		f.createNewFile();
		System.out.println(f.canRead());
		OutputStream out = new FileOutputStream(f);

		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = inputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}

		inputStream.close();
		out.flush();
		out.close();
		//f.deleteOnExit();
		return fileName;
		
	}


}
