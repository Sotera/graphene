package graphene.dao;

/**
 * 
 * The UnifiedEntity class reflects a denormalized entity table made from the
 * EntityRef tables. It has it's drawbacks, and there are some things that can't
 * be done here which the EntityRef structure allows (especially when EntityRef
 * has been put into a graph db)
 * 
 * Put anything here that you'd want this DAO to do differently than the generic
 * one (Such as methods returning view objects instead of model objects)
 * 
 * @author djue
 * 
 * @param <T>
 * @param <Q>
 */
public interface UnifiedEntityDAO<T, Q> extends GenericDAO<T, Q> {

}
