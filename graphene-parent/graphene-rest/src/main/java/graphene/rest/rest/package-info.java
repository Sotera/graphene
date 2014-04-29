/**
 * So this package may look peculiar.  The root namespace of this 
 * submodule is graphene.rest.
 * 
 * This being a module with Tapestry enabled, (and more specifically, one with 
 * RESTEasy integration included) there are certain packages that will be scanned 
 * for classes to be treated in different ways.
 * A package called 'rest' sibling to 'services' will be looked for by default.
 * If any java classes or interfaces are found here, Tapestry (with the RESTEasy 
 * integration) will attempt to treat those files as REST resource definitions.
 * This fails for interfaces, since they can't be instantiated (this may be a bug)
 * 
 * The original decision to create interfaces with the REST annotations, and then 
 * concrete implementations (that don't need ANY REST annotations to work)
 * was because we thought we might have different implementations for the same
 *  interface.  So far this has not be the case except in rare test conditions,
 * where a test implementation was bound to the real interface.
 * 
 * 
 * Putting the implementations in this package would require the implementations 
 * to know about RESTEasy annotations, but it lessens the amount of configuration 
 * even more. (It would also mark the end of needing REST interface classes). 
 *  It may also mean more difficult testing, but not necessarily (mocked data 
 *  providers can be substituted!!)
 */
package graphene.rest.rest;

