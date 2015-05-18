package graphene.web.model;

import java.io.Serializable;

// A handy holder for an (id, version).

public class IdVersion implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Integer version;

	public IdVersion(Long id, Integer version) {
		super();
		id = id;
		version = version;
	}

	public Long getId() {
		return id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setId(Long id) {
		id = id;
	}

	public void setVersion(Integer version) {
		version = version;
	}

	@Override
	public String toString() {
		final String DIVIDER = ", ";

		final StringBuilder buf = new StringBuilder();
		buf.append(this.getClass().getSimpleName() + ": ");
		buf.append("[");
		buf.append("id=" + id + DIVIDER);
		buf.append("version=" + version);
		buf.append("]");
		return buf.toString();
	}

}
