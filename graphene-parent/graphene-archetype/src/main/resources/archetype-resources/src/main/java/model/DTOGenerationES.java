package ${package}.model;

import graphene.dao.DataSourceListDAO;
import graphene.dao.es.ESRestAPIConnection;
import ${package}.dao.InstagramDAOModule;
import graphene.model.GenerateModel;
import graphene.util.Triple;
import graphene.util.Tuple;
import graphene.util.UtilModule;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.mapping.GetMapping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DTOGenerationES {
	/**
	 * Run this to create the DTOs. If you have datasets called widgets and
	 * whizbangs, it may be best to put them in different packages, as specified
	 * by the package string below.
	 */
	public static void main(final String[] args) throws Exception {
		final DTOGenerationES generator = new DTOGenerationES();
		generator.generateFromURL();
		return;
	}

	GenerateModel gm;

	String outputDirectory = "src/main/java";
	ESRestAPIConnection c;

	static ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY); // can

	private Registry registry;

	public final static String baseURL = "";

	private static final boolean OVERWRITE = true;

	/**
	 * This version is for using json that has already been modified for POJO
	 * creation by adding the type:object parameter.
	 * 
	 * @param gm
	 * @param outputDirectory
	 * @throws MalformedURLException
	 * @throws Exception
	 */
	public static void generateFromFiles(final GenerateModel gm, final String outputDirectory)
			throws MalformedURLException, Exception {

	}

	List<Triple<String, String, String>> convertToArrays;
	List<Tuple<String, String>> standardizeNames;

	Logger logger = LoggerFactory.getLogger(DTOGenerationES.class);

	private DataSourceListDAO dataSourceDAO;

	public DTOGenerationES() {
		setup();

	}

	private void buildFromES(final String typeName, final String outputDirectory) throws Exception {
		final JestClient client = c.getClient();
		final io.searchbox.indices.mapping.GetMapping.Builder g = new GetMapping.Builder();
		g.addIndex("${projectName}");
		g.addType(typeName);
		final GetMapping getMapping = g.build();
		System.out.println(getMapping.toString());
		final JestResult jestResult = client.execute(getMapping);
		final JsonNode node = mapper.readValue(jestResult.getJsonString(), JsonNode.class);
		final ObjectNode currentType = (ObjectNode) node.findValue(typeName);
		// This is the name of the class for the report,
		final String reportClassName = typeNameToClassName(typeName);
		// and also the package it's subobjects will be created in.
		final String packageName = "${package}.model.${projectName}." + reportClassName;

		// This is the meat of the operation, modifying the elastic search JSON
		// to do what we want.
		final ObjectNode modifiedJsonTree = modifyESJSON(currentType, packageName);

		// ES for ElasticSearch
		// Build the classes.
		gm.buildModel(modifiedJsonTree.toString(), reportClassName, packageName, outputDirectory);
	}

	/**
	 * TODO: make a query to get all the types in the index, then loop over the
	 * results and perform these builds.
	 * 
	 * @throws MalformedURLException
	 * @throws Exception
	 */
	public void generateFromURL() throws MalformedURLException, Exception {
		for (final String d : dataSourceDAO.getAvailableTypes()) {
			buildFromES(d, outputDirectory);
		}
		c.getClient().shutdownClient();
	}

	/**
	 * This is needed for POJO creation and is not present in the json schema
	 * from ES.
	 * 
	 * @param packageName
	 * 
	 * @param jsonString
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private ObjectNode modifyESJSON(final ObjectNode node, final String packageName) throws JsonParseException,
			JsonMappingException, IOException {

		if (node == null) {
			logger.error("Couldn't make an ObjectNode out of " + node);

		} else {
			// The object node passed in needs to have a properties field if we
			// are to modify it.
			final ObjectNode mainProperties = (ObjectNode) node.findValue("properties");
			if (mainProperties != null) {
				node.put("type", "object");
				final Iterator<Entry<String, JsonNode>> iterator = mainProperties.fields();
				while (iterator.hasNext()) {
					final Entry<String, JsonNode> entry = iterator.next();
					// First look into ALL sub objects
					logger.debug("Traversing on type " + entry.getKey());
					final JsonNode newNode = modifyESJSON((ObjectNode) entry.getValue(), packageName);
					if (newNode != null) {
						mainProperties.put(entry.getKey(), newNode);
					}
					/**
					 * Look for any properties which are supposed to be array
					 * objects. ES doesn't tell us whether or not these fields
					 * will behave as arrays, we happen to know this ahead of
					 * time. You'll only know when it's potentially an array
					 * when you do an actual query for results. This is
					 * recursive; the array objects could be at any level.
					 */
					logger.debug("Looking for array types in " + entry.getKey());
					// then finally convert any at the current level.
					for (final Triple<String, String, String> objectToConvert : convertToArrays) {
						if (objectToConvert.getFirst().equals(entry.getKey())) {
							// found an object to convert
							final ObjectNode nodeToConvert = (ObjectNode) mainProperties
									.get(objectToConvert.getFirst());
							if (nodeToConvert != null) {
								final String newClassName = packageName + "." + objectToConvert.getThird();
								logger.debug("Found object to convert '" + objectToConvert.getFirst()
										+ "' in json node and will be made into class " + newClassName);
								final ObjectNode arrayContainer = mapper.createObjectNode();
								nodeToConvert.put("type", "object");
								nodeToConvert.put("javaType", newClassName);
								arrayContainer.set("items", nodeToConvert);
								arrayContainer.put("type", "array");
								mainProperties.replace(objectToConvert.getFirst(), arrayContainer);
							}
						}
					}

					/**
					 * Fix the names of non-array elements. This affects the
					 * class name that is generated, but doesn't seem to affect
					 * the property name unfortunately.
					 */
					for (final Tuple<String, String> nameToStandardize : standardizeNames) {
						if (nameToStandardize.getFirst().equals(entry.getKey())) {
							final ObjectNode nodeToConvert = (ObjectNode) mainProperties.get(nameToStandardize
									.getFirst());
							if (nodeToConvert != null) {
								final String newClassName = packageName + "." + nameToStandardize.getSecond();
								logger.debug("Found name to standardize: '" + nameToStandardize.getFirst()
										+ "' in json node and will be made into class " + newClassName);
								nodeToConvert.put("type", "object");
								nodeToConvert.put("javaType", newClassName);
								mainProperties.replace(nameToStandardize.getFirst(), nodeToConvert);
							}
						}
					}
				}
			}
		}
		return node;
	}

	public void setup() {

		final RegistryBuilder builder = new RegistryBuilder();
		builder.add(DTOGenerationModule.class);
		builder.add(InstagramDAOModule.class);
		builder.add(UtilModule.class);
		registry = builder.build();
		registry.performRegistryStartup();
		gm = new GenerateModel(OVERWRITE);
		dataSourceDAO = registry.getService(DataSourceListDAO.class);
		c = registry.getService(ESRestAPIConnection.class);

		standardizeNames = new ArrayList<Tuple<String, String>>();
		// standardizeNames.add(new Tuple<String, String>("ATTACHMENTS",
		// "AttachementContainer"));
		/**
		 * We are creating a triple of String,String,String
		 * 
		 * The first String is the field we expect to see in the JSON and want
		 * to modify.
		 * 
		 * The second String is an explicit field name for the list of things
		 * 
		 * The second String is an explicit class name we will give this field
		 * so that the jsonscheme2pojo's Inflector class will not give us a bad
		 * name.
		 * 
		 * For a, b, c we'd get something like
		 * 
		 * @JsonProperty("a") private List<c> b = new ArrayList<c>();
		 * 
		 * 
		 *                    For instance an array of SubjectAddress gets
		 *                    filled with objects called SubjectAddres <-- with
		 *                    one 's' at the end. It is trying to de-pluralize
		 *                    the field that we are saying is an array. What is
		 *                    important for us is that the bean class names are
		 *                    actually correct, since Tapestry might use them to
		 *                    describe the bean. HomeAddress becomes 'Home
		 *                    Address' as the label for the bean, etc.
		 */
		convertToArrays = new ArrayList<Triple<String, String, String>>();
		convertToArrays.add(new Triple<String, String, String>("comments", "comments", "Comments"));

	} // share

	private String typeNameToClassName(final String typeName) {
		return typeName.toUpperCase().replaceAll("-", "").replaceAll("_", "").replaceAll("TYPE", "");
	}

}
