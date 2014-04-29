/**
 * This package houses classes for batch import and regular bulk import into Neo4J from an RDBMS.  This is the 2nd and 3rd generation, respectively.
 * 
 * Note however that the 2nd generation batch import implementations are much much faster than what I'm calling the 'regular bulk import' classes, or 3rd gen.
 *  
 * This is because the 3rd gen classes make direct use of Neo4J indexes to check for uniqueness, explicitly, while in the 2nd generation batch import, the class determines uniqueness via a series of bloom filters.
 * The 3rd gen classes make it easy to deal with multiple relations, and multiple indexes. (or at least it's easier to reason about them)
 * 
 * The 2nd generations batch importers have the suffix 'DirectImport' and are based on the Importer class in org.neo4j.batchimport.
 * These versions, however, skip the whole laborious process of creating CSV file intermediaries, and use bloom filters to bring unique nodes directly from an iterable source into the Neo4J.
 * With these versions it is harder to deal with populating multiple indicies at batch time.  Further inspection of Neo4J internals is required to see how this works.
 * 
 * 
 */
package graphene.ingest.neo4j;

