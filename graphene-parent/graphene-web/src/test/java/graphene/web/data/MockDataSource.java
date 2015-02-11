package graphene.web.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Essentially a DAO Implementation or Service implementation, filled with fake
 * data. In real life you'd be pulling from some data store.
 * 
 * This was copied into Graphene from the internet, to act as a template for
 * understanding the grid datasource and pieces that back it.
 * 
 * @author djue
 * 
 */
public class MockDataSource implements IDataSource {
	private List<Celebrity> celebrities = new ArrayList<Celebrity>();
	private final Logger logger = LoggerFactory.getLogger(MockDataSource.class);

	public MockDataSource() throws ParseException {
		final DateTimeFormatter sdf = DateTimeFormat.forPattern("mm/DD/YYYY");
		for (int i = 0; i < 50; i++) {
			addCelebrity(new Celebrity("Britney" + i, "Spearce", DateTime.parse("12/02/" + (1981 + i), sdf),
					Occupation.SINGER));
			addCelebrity(new Celebrity("Bill", "Clinton", DateTime.parse("08/19/" + (1946 + i), sdf),
					Occupation.POLITICIAN));
			addCelebrity(new Celebrity("Placido" + i, "Domingo", DateTime.parse("01/21/" + (1941 + i), sdf),
					Occupation.SINGER));
			addCelebrity(new Celebrity("Albert" + i, "Einstein", DateTime.parse("03/14/" + (1879 + i), sdf),
					Occupation.SCIENTIST));
			addCelebrity(new Celebrity("Ernest" + i, "Hemingway", DateTime.parse("07/21/" + (1899 + i), sdf),
					Occupation.WRITER));
			addCelebrity(new Celebrity("Luciano" + i, "Pavarotti", DateTime.parse("10/12/" + (1935 + i), sdf),
					Occupation.SINGER));
			addCelebrity(new Celebrity("Ronald" + i, "Reagan", DateTime.parse("02/06/" + (1911 + i), sdf),
					Occupation.POLITICIAN));
			addCelebrity(new Celebrity("Pablo" + i, "Picasso", DateTime.parse("10/25/" + (1881 + i), sdf),
					Occupation.ARTIST));
			addCelebrity(new Celebrity("Blaise" + i, "Pascal", DateTime.parse("06/19/" + (1623 + i), sdf),
					Occupation.SCIENTIST));
			addCelebrity(new Celebrity("Isaac" + i, "Newton", DateTime.parse("01/04/" + (1643 + i), sdf),
					Occupation.SCIENTIST));
			addCelebrity(new Celebrity("Antonio" + i, "Vivaldi", DateTime.parse("03/04/" + (1678 + i), sdf),
					Occupation.COMPOSER));
			addCelebrity(new Celebrity("Niccolo" + i, "Paganini", DateTime.parse("10/27/" + (1782 + i), sdf),
					Occupation.MUSICIAN));
			addCelebrity(new Celebrity("Johannes" + i, "Kepler", DateTime.parse("12/27/" + (1571 + i), sdf),
					Occupation.SCIENTIST));
			addCelebrity(new Celebrity("Franz" + i, "Kafka", DateTime.parse("07/03/" + (1883 + i), sdf),
					Occupation.WRITER));
			addCelebrity(new Celebrity("George" + i, "Gershwin", DateTime.parse("09/26/" + (1898 + i), sdf),
					Occupation.COMPOSER));
		}
	}

	@Override
	public void addCelebrity(final Celebrity c) {
		final long newId = celebrities.size();
		c.setId(newId);
		celebrities.add(c);
	}

	@Override
	public void filter(final String value) {
		try {
			celebrities = new MockDataSource().getAllCelebrities();

			if ((value == null) || (value.length() == 0)) {
				return;
			}
			final List<Celebrity> result = new ArrayList<Celebrity>();
			for (final Celebrity c : celebrities) {
				if (c.getFirstName().toUpperCase().contains(value.toUpperCase())
						|| c.getLastName().toUpperCase().contains(value.toUpperCase())
						|| c.getOccupation().toString().toUpperCase().contains(value.toUpperCase())) {

					result.add(c);
				}
			}
			celebrities = result;
		} catch (final ParseException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public List<Celebrity> getAllCelebrities() {
		return celebrities;
	}

	@Override
	public Celebrity getCelebrityById(final long id) {
		for (final Celebrity c : celebrities) {
			if (c.getId() == id) {
				return c;
			}
		}
		return null;
	}

	@Override
	public List<Celebrity> getRange(final int indexFrom, final int indexTo) {
		final List<Celebrity> result = new ArrayList<Celebrity>();
		for (int i = indexFrom; i <= indexTo; i++) {
			result.add(celebrities.get(i));
		}
		return result;
	}
}
