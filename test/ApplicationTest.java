import jobs.StyleColorImporter;

import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ApplicationTest extends FunctionalTest {

	//@Test
	public void testThatIndexPageWorks() {
		Response response = GET("/");
		assertIsOk(response);
		assertContentType("text/html", response);
		assertCharset(play.Play.defaultWebEncoding, response);
	}
	@Test
	public void test(){
		 new StyleColorImporter().now();
	}


	//@Test
	public void makeColorTable(){
		int count = 0;
		Color.deleteAll();
		for (int i = 0 ; i < 256; i ++){

			Color color = new Color();
		color.color_id = count;
		count++;
		color.value = "ff"+buildColorFromNumber(i)+"00";
		color.save();
		}
		for (int i = 255 ; i >-1; i --){
			Color color = new Color();
			color.color_id = count;
			count++;    	
			color.value = buildColorFromNumber(i)+"ff00";
			color.save();
		}
		for (int i = 0 ; i < 256; i ++){
			Color color = new Color();
			color.color_id = count;
			count++;    	
			color.value = "00ff"+buildColorFromNumber(i);
			color.save();
		}
		for (int i = 0 ; i < 255; i ++){
			Color color = new Color();
			color.color_id = count;
			count++;    
			color.value = "00"+buildColorFromNumber(i)+"ff";
			color.save();
		}

		for (int i = 0 ; i < 256; i ++){
			Color color = new Color();
			color.color_id = count;
			count++;
			color.value = buildColorFromNumber(i)+"00ff";
			color.save();
		}
	}




	public static String buildColorFromNumber(int i){
		int sixteensPlace = i/16;;
		int onesPlace = i - (sixteensPlace*16);
		return getStrFromInt(sixteensPlace)+getStrFromInt(onesPlace);
	}

	public static String getStrFromInt(int i){
		if (i>9){
			String hexColorValue = "";
			if (i==10){
				hexColorValue = "a";
			} else if (i ==11){
				hexColorValue = "b";
			} else if (i ==12){
				hexColorValue = "c";
			} else if (i ==13){
				hexColorValue = "d";
			} else if (i ==14){
				hexColorValue = "e";
			} else if (i ==15){
				hexColorValue = "f";
			}
			return hexColorValue;
		}
		return Integer.toString(i);
	}



}