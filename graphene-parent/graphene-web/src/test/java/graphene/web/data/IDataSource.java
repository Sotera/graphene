package graphene.web.data;

import java.util.List;

/**
 * Essentially a DAO or Service interface. 
 * 
 * This was copied into Graphene from the internet,
 * to act as a template for understanding the grid datasource and pieces that
 * back it.
 * 
 * @author djue
 * 
 */
public interface IDataSource {
	public List<Celebrity> getAllCelebrities();

	public Celebrity getCelebrityById(long id);

	public void addCelebrity(Celebrity c);

	public List<Celebrity> getRange(int indexFrom, int indexTo);

	public void filter(String value);
}
