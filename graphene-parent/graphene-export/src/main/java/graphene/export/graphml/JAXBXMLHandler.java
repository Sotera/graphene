package graphene.export.graphml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Simple handler to marshal/unmarshal XML text in JAXB. This is necessary to ensure the XML text
 * is actually output as XML, as opposed to having &lt; and &gt; replacements everywhere.
 * 
 * @author pmoore
 *
 */
public class JAXBXMLHandler implements DomHandler<String, StreamResult> {
    private static final String JSON_START_TAG = "<json>";
    private static final String JSON_END_TAG = "</json>";
 
    private StringWriter xmlWriter = new StringWriter();
 
    public StreamResult createUnmarshaller(ValidationEventHandler errorHandler) {
        return new StreamResult(xmlWriter);
    }
 
    public String getElement(StreamResult rt) {
        String xml = rt.getWriter().toString();
        int beginIndex = xml.lastIndexOf(JSON_START_TAG) + JSON_START_TAG.length();
        int endIndex = xml.lastIndexOf(JSON_END_TAG);
        return xml.substring(beginIndex, endIndex);
    }
 
    public Source marshal(String n, ValidationEventHandler errorHandler) {
        try {
            String xml = JSON_START_TAG + n.trim() + JSON_END_TAG;
            StringReader xmlReader = new StringReader(xml);
            return new StreamSource(xmlReader);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}