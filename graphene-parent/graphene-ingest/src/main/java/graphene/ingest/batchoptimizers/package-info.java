/**
 * The purpose of this package is for classes that take RDBMS tables and output them in an particular fashion to multiple CSV files, so that the traditional bulk import via CSVs can be accomplished.
 * 
 * This is part of the 1st generation batch import attempts tried because it was the only kind with examples.  It is error prone because of this two stage process, having to deal with files as intermediaries, and difficulties in creating many relationships.
 * Instead, we now (2nd generation) implement what are called "DirectImporters" for the different data tables. 
 * 
 */
package graphene.ingest.batchoptimizers;