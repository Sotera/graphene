package graphene.web.model;

import java.util.List;

import org.apache.tapestry5.tree.TreeModelAdapter;

public class MenuItemTreeModelAdapter implements TreeModelAdapter<MenuItem> {

	@Override
	public List<MenuItem> getChildren(final MenuItem value) {
		return value.getChildren();
	}

	@Override
	public String getLabel(final MenuItem value) {
		return value.getLabel();
	}

	@Override
	public boolean hasChildren(final MenuItem value) {
		return !value.isLeaf();
	}

	@Override
	public boolean isLeaf(final MenuItem value) {
		return value.isLeaf();
	}

}
