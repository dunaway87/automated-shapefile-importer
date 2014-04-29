package jobs;

import models.Color;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.EPSGFinder;
@OnApplicationStart
public class InitEPSGAddition extends Job{



	public void doJob(){
		JPA.em().createNativeQuery("CREATE Table if not exists epsg_lookup (srid integer NOT NULL,  auth_name character varying(256),  auth_srid integer,  srtext character varying(2048),  proj4text character varying(2048))").executeUpdate();
		if (EPSGFinder.needsImport()){
			new EPSGAdder().now();
		}
	}
}
