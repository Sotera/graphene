package graphene.hts.entityextraction;

import java.util.Collection;

public interface Extractor {


	public abstract Collection<String> extract(String source);

	public abstract String getIdType();

	public abstract String getNodetype();

	public abstract String getRelationType();

	public abstract String getRelationValue();

}