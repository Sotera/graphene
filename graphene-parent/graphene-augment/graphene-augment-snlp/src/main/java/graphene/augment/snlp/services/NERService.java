package graphene.augment.snlp.services;

import graphene.augment.snlp.model.NERResult;

import java.util.List;

public interface NERService {

	public abstract List<NERResult> getResults(String text);

}