#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

/**
 * This package holds classes for a particular type of document.  The document/record is read in via a DAO implementation and then these classes get populated either manually or automatically through deserialization (i.e. Json->Java)
 * 
 * A best practice is to separate related classes by the realm of data that they deal with - i.e. All Instagram related classes go in one package, and Facebook ones go in another, etc.  This is important in case the format of the data changes in one and not the other, or if the realms have a different convention for the same terms.
 * @author djue
 *
 */
package ${package}.model.media;