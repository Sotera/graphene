package graphene.dao;

/**
 * Put anything here that you'd want this DAO to do differently than the generic
 * one (Such as methods returning view objects instead of model objects)
 * 
 * @author djue
 * 
 * @param <T>
 * @param <Q>
 */
public interface NormalizedEntityDAO<T, Q> extends GenericDAO<T,Q> {

}
