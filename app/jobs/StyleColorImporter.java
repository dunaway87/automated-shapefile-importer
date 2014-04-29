package jobs;

import javax.swing.text.DefaultEditorKit.InsertContentAction;

import models.Color;
import play.jobs.Job;

public class StyleColorImporter extends Job{



	public static int count;

	public void doJob(){
		importColors();
	}

	public static void importColors(){
		count = 0;
		String prefix = "";
		String sufix = "";
		Color.deleteAll();

		prefix = "ff";
		sufix = "00";
		insertCountingUp(prefix, sufix);


		prefix = "";
		sufix = "ff00";
		insertCountingDown(prefix, sufix);
		prefix = "00ff";
		sufix = "";
		insertCountingUp(prefix, sufix);


		prefix = "00";
		sufix = "ff";
		insertCountingDown(prefix, sufix);

		prefix = "";
		sufix = "00ff";
		insertCountingUp(prefix, sufix);

	}


	public static void insertCountingUp(String prefix, String sufix){
		for (int i = 0 ; i < 256; i ++){
			Color color = new Color();
			color.color_id = count;
			count++;
			//System.out.println(count);
			color.value = prefix+buildColorFromNumber(i)+sufix;
			color.save();
		}
	}
	public static void insertCountingDown(String prefix, String sufix){
		for (int i = 254 ; i >0; i --){

			Color color = new Color();
			color.color_id = count;
			count++;    	
			color.value = prefix+buildColorFromNumber(i)+sufix;
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

	public static boolean needsImport() {

		long records = Color.count();
		if (records >0){
			return false;
		} else return true;




	}


}

