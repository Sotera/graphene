package graphene.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ValueEncoder;

// This encoder is intended for use storing a list of (id, version) in a single Hidden field:
// - during render, to convert a list of (id, version) to a String to be stored by the Hidden.
// - during form submission, to convert the String back to a list of (id, version) for use server-side.

public class IdVersionEncoder implements ValueEncoder<List<IdVersion>> {
	private final String DELIMITER = ":";

	@Override
	public String toClient(final List<IdVersion> idVersions) {
		String s = "";

		for (final IdVersion idVersion : idVersions) {
			s += idVersion.getId() + DELIMITER + idVersion.getVersion() + DELIMITER;
		}

		return s;
	}

	@Override
	public List<IdVersion> toValue(final String idVersionsAsString) {
		final List<IdVersion> idVersions = new ArrayList<IdVersion>();

		final String[] chunks = idVersionsAsString.split(DELIMITER);

		for (int i = 0; i < chunks.length; i = i + 2) {
			final Long id = Long.parseLong(chunks[i]);
			final Integer version = chunks[i + 1].equals("null") ? null : Integer.parseInt(chunks[i + 1]);
			idVersions.add(new IdVersion(id, version));
		}

		return idVersions;
	}

}