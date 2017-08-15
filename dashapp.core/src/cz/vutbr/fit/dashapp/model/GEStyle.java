package cz.vutbr.fit.dashapp.model;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

/**
 * Style of graphical element.
 * 
 * For web-app compatibility.
 * 
 * @author Jiri Hynek
 *
 */
@Root(name="style")
@Convert(GEStyleConverter.class)
public class GEStyle {
	
	public static final Map<String, String> EMPTY_MAP = new HashMap<>();
	
	private Map<String, String> styleItems;
	
	public void putStyleItem(String key, String value) {
		if(styleItems == null) {
			styleItems = new HashMap<>();
		}
		styleItems.put(key, value);
	}
	
	public Map<String, String> getStyleItems() {
		return styleItems == null ? EMPTY_MAP : styleItems;
	}
	
	public String getValue(String key) {
		return styleItems == null ? null : styleItems.get(key);
	}

}
