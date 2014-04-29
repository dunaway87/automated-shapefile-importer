package jobs;

import models.Color;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
@OnApplicationStart
public class InitStyleColors extends Job{



	public void doJob(){
		if (StyleColorImporter.needsImport()){
			new StyleColorImporter().now();
		}
	}
}
