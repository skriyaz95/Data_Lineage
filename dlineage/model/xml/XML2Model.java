package demos.dlineage.model.xml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class XML2Model {

	public static columnImpactResult loadXML(String xml) {
		Serializer serializer = new Persister();
		try {
			return serializer.read(columnImpactResult.class, xml);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}