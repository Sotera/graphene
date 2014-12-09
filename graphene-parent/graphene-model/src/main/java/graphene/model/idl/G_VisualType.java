package graphene.model.idl;

/**
 * A set of canonical types/tags for visual pages (T5 page classes). This is
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
 * 
 */
public enum G_VisualType {
	TOP, CHART, DEFAULT, GRAPH, HIDDEN, IMAGE, INFINITE_SCROLL, LIST, MONEY, PLUGIN, SEARCH, TEXT, WEBGL, EVENT, META, DEMO, REDACTED, GEO, EXPERIMENTAL, SETTINGS, ADMIN
}
