/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package graphene.model.idl;  
@SuppressWarnings("all")
/** * A set of canonical types/tags for visual pages (T5 page classes). This is
	 * mostly for helping with menu selection, menu organization and styling with
	 * css or icons.
	 * 
	 * Later on, a user might be able to search for a particular type of visual
	 * display, and we would show a list of pages that support that display.
	 * 
	 * Might be changed to G_VisualTag, so that annotations can suggest multiple per
	 * page/component.
	 * 
	 * 
	 * HIDDEN will cause a page to not be displayed in automatically generated
	 * menus.
	 * 
	 * @author djue
	 * */
@org.apache.avro.specific.AvroGenerated
public enum G_VisualType { 
  HELP, TOP, CHART, DEFAULT, GRAPH, HIDDEN, IMAGE, INFINITE_SCROLL, LIST, MONEY, PLUGIN, SEARCH, TEXT, WEBGL, EVENT, META, DEMO, REDACTED, GEO, EXPERIMENTAL, SETTINGS, MANAGE_WORKSPACES, ADMIN, VIEW_WORKSPACE  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"G_VisualType\",\"namespace\":\"graphene.model.idl\",\"doc\":\"* A set of canonical types/tags for visual pages (T5 page classes). This is\\r\\n\\t * mostly for helping with menu selection, menu organization and styling with\\r\\n\\t * css or icons.\\r\\n\\t * \\r\\n\\t * Later on, a user might be able to search for a particular type of visual\\r\\n\\t * display, and we would show a list of pages that support that display.\\r\\n\\t * \\r\\n\\t * Might be changed to G_VisualTag, so that annotations can suggest multiple per\\r\\n\\t * page/component.\\r\\n\\t * \\r\\n\\t * \\r\\n\\t * HIDDEN will cause a page to not be displayed in automatically generated\\r\\n\\t * menus.\\r\\n\\t * \\r\\n\\t * @author djue\\r\\n\\t *\",\"symbols\":[\"HELP\",\"TOP\",\"CHART\",\"DEFAULT\",\"GRAPH\",\"HIDDEN\",\"IMAGE\",\"INFINITE_SCROLL\",\"LIST\",\"MONEY\",\"PLUGIN\",\"SEARCH\",\"TEXT\",\"WEBGL\",\"EVENT\",\"META\",\"DEMO\",\"REDACTED\",\"GEO\",\"EXPERIMENTAL\",\"SETTINGS\",\"MANAGE_WORKSPACES\",\"ADMIN\",\"VIEW_WORKSPACE\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
}
