package graphene.model.view.sample;

public class DisplayNode {
	private Long id;
	private String name;

	public DisplayNode() {
		// TODO Auto-generated constructor stub
	}

	public DisplayNode(final Long id, final String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public final Long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) {
		this.name = name;
	}
}
