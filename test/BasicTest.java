import org.junit.*;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import MyTestsNotForRossEyes.GetUUIDS;

import java.sql.SQLException;
import java.util.*;

import play.Logger;
import play.libs.WS;
import play.mvc.Scope.Params;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Test
    public void aVeryImportantThingToTest() throws SQLException {
   /*   JsonArray array =  GetUUIDS.getShapefileNames();
       Logger.info("%s", array);
    	
    	for (int i = 0; i < 1; i++) {
			JsonObject obj = array.get(i).getAsJsonObject();
			String layer = obj.get("layer").getAsString();
			String uuids = obj.get("uuids").getAsString();
			Logger.info("layer  %s", layer);
			Logger.info("uuids  %s", uuids);
	        Map<String, Object> m = new HashMap<String, Object>();
	        m.put("uuid", layer);
	        m.put("fileList", uuids);

		
			WS.url("http://localhost:9001/importFromMongo").params(m).post();
    	}*/
    	
    	
    }

}
