package cz.vutbr.fit.dashapp.model;

import java.util.Map.Entry;
import java.util.Set;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

/**
 * Conversion of custom GE style elements. 
 * 
 * @author Jiri Hynek
 *
 */
public class GEStyleConverter implements Converter<GEStyle> {

	@Override
	public GEStyle read(InputNode node) throws Exception {
		GEStyle geStyle = new GEStyle();
		InputNode next = node.getNext();
		while(next != null) {
			geStyle.putStyleItem(next.getName(), next.getValue());
			next = node.getNext();
		}
		return geStyle;
	}

	@Override
	public void write(OutputNode node, GEStyle styleItem) throws Exception {
		Set<Entry<String, String>> items = styleItem.getStyleItems().entrySet();
		for (Entry<String, String> item : items) {
			node.getChild(item.getKey()).setValue(item.getValue());
		}
	}
	
}