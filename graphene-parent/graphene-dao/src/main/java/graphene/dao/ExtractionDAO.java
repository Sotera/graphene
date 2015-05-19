package graphene.dao;

import graphene.model.extracted.CrossReferenceResult;
import graphene.model.extracted.EncodedFile;
import graphene.model.extracted.ExtractedData;
import graphene.model.idl.G_CallBack;
import graphene.model.idl.G_EntityQuery;

public interface ExtractionDAO {
	long count(final G_EntityQuery q) throws Exception;

	public boolean exists(final String id);

	ExtractedData getExtractedData();

	public boolean performCallback(final long offset, final long maxResults, final G_CallBack cb, final G_EntityQuery q);

	/**
	 * TODO: Move this to a crossreference dao.
	 * 
	 * @param results
	 */
	void saveCrossReferenceResults(CrossReferenceResult results);

	void saveEncodedFile(final EncodedFile ef);

	void saveExtraction(ExtractedData ed);
}
