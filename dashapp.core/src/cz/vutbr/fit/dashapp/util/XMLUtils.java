package cz.vutbr.fit.dashapp.util;

import java.io.StringWriter;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class XMLUtils {
	
	public static String serialize(DashboardFile dashboardFile) {
		return serialize(dashboardFile.getDashboard(false));
	}
	
	/**
	 * 
	 * @param dashboard
	 * @return
	 */
	public static String serialize(Dashboard dashboard) {
		Serializer serializer = new Persister();
		StringWriter writer = new StringWriter();
		try {
			serializer.write(dashboard, writer);
		} catch (Exception e) {
			System.err.println("Unable to serialize dashboard");
		}
		return writer.toString();
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	public static Dashboard deserialize(String xml) {
		Serializer serializer = new Persister();
		Dashboard newDashboard = null;
		try {
			newDashboard = serializer.read(Dashboard.class, xml);
		} catch (Exception e) {
			System.err.println("Unable to deserialize dashboard");
		}
		return newDashboard;
	}

}
