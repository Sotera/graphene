package graphene.augment.snlp.web.pages;

import graphene.augment.snlp.model.SentimentResult;
import graphene.augment.snlp.model.StringWithSentiment;
import graphene.augment.snlp.services.SentimentAnalyzer;
import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.slf4j.Logger;

@PluginPage(visualType = G_VisualType.EXPERIMENTAL, menuName = "Sentiment", icon = "fa fa-lg fa-fw fa-comments")
@Import(library = { "context:core/js/plugin/dropzone/dropzone.js", "context:/core/js/startdropzone.js" })
public class Sentiment {
	@Inject
	private Request request;
	@Property
	private UploadedFile file;
	@Inject
	private Logger logger;
	@Inject
	private SentimentAnalyzer sa;
	@Property
	private int index;
	@Property
	private StringWithSentiment currentSentiment;

	@Property
	@Persist
	private boolean highlightZoneUpdates;

	@InjectComponent
	private Zone listZone;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	@Persist
	@Property
	private SentimentResult sr;

	public String getZoneUpdateFunction() {
		return highlightZoneUpdates ? "highlight" : "show";
	}

	public Object onSuccessFromMydropzone() {

		final BufferedReader br = new BufferedReader(new InputStreamReader(file.getStream()));
		String line;
		final List<StringWithSentiment> list = new ArrayList<StringWithSentiment>();
		try {
			while ((line = br.readLine()) != null) {
				logger.debug(line);
				list.add(sa.findSentiment(line));
			}
		} catch (final IOException e) {
			logger.error("onSuccessFromMydropzone" + e.getMessage());
		}
		sr = new SentimentResult("asdf", list);

		if (request.isXHR()) {
			logger.debug("Rendering AJAX response");
			ajaxResponseRenderer.addRender(listZone);
		}
		return this;
	}
}
