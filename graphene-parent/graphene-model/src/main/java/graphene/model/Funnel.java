package graphene.model;

/**
 * Funnel classes are for converting model pojos into a view object. Instantiate
 * one of these when you want to convert between a customer specific datatype
 * and one of the core models.
 * 
 * The intent is that you have a separate funnel for each pair of items you want
 * to convert between. Most of the time you would only want to convert one way.
 * 
 * @author djue
 * 
 */
public interface Funnel<TO, FROM> {
	TO from(FROM f);
}
