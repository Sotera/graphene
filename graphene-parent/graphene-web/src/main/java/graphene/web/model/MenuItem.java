package graphene.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuItem {
	private String icon = "";
	private String pageName;
	public final String uuid = UUID.randomUUID().toString();
	private final List<MenuItem> children = new ArrayList<MenuItem>();
	private String label = "";

	public MenuItem() {
		// TODO Auto-generated constructor stub
	}

	public MenuItem(final String icon, final String label, final String pageName) {
		super();
		this.icon = icon;
		this.label = label;
		this.pageName = pageName;
	}

	public MenuItem addChild(final MenuItem child) {
		children.add(child);
		return this;
	}

	/**
	 * @return the children
	 */
	public List<MenuItem> getChildren() {
		return children;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the pageName
	 */
	public String getPageName() {
		return pageName;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

	public MenuItem seek(final String uuid) {
		if (this.uuid.endsWith(uuid)) {
			return this;

		} else {
			for (final MenuItem c : children) {
				final MenuItem match = c.seek(uuid);
				if (match != null) {
					return match;
				}
			}
		}
		return null;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(final String icon) {
		this.icon = icon;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * @param pageName
	 *            the pageName to set
	 */
	public void setPageName(final String pageName) {
		this.pageName = pageName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MenuItem [icon=" + icon + ", pageName=" + pageName + ", uuid=" + uuid + ", children=" + children
				+ ", label=" + label + "]";
	}

}
