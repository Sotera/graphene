package graphene.model;

import java.io.File;
import java.net.URL;

import org.jsonschema2pojo.SchemaMapper;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

/**
 * Class to generate POJOs from Elastic search schema mappings, as provided
 * through the ES rest interface.
 * 
 * Step 0: get POJO generation working. Currently requires shaving off two outer
 * layers of json.
 * 
 * Step 1: modularize so that all customer implementations can use this as part
 * of the inclusion of the Graphene-DAO-ES module.
 * 
 * Step 2: Pull the json directly from ES, and pass the modified json to the
 * SchemaMapper, bypassing the need for saving json files.
 * 
 * Step 3: Make this similar to QueryDSL, where the class name is pulled from
 * the JSON and Directories are auto created based on package name.
 * 
 * @author djue
 * 
 */
public class GenerateModel {

	public void buildModel(URL source, String className, String packageName,
			String outputDirectory) throws Exception {
		JCodeModel codeModel = new JCodeModel();
		System.out.println("Attempting to build POJOs for type " + className
				+ " from source " + source.getPath());
		JType generate = new SchemaMapper().generate(codeModel, className,
				packageName, source);
		File f = new File(outputDirectory);
		if (f.isDirectory()) {
			File existingFile = new File(getExistingFileURL(
					f.getAbsolutePath(), packageName, className));
			if (existingFile.exists()) {
				System.out.println("Skipping " + existingFile.getAbsolutePath()
						+ " because it already exists.");
			} else {
				System.out.println("No existing file found at "
						+ existingFile.getAbsolutePath());
				System.out.println("Building model at " + f.getAbsolutePath());
				codeModel.build(f);
			}
			System.out.println("done");
		} else {
			System.out.println("ERROR: Path must be an existing directory");
		}

	}

	private String getExistingFileURL(String outputDirectory,
			String packageName, String className) {
		String packagePath = packageName.replaceAll("[.]", "//");
		String existingFile = outputDirectory + "//" + packagePath + "//"
				+ className + ".java";
		System.out
				.println("Potentially existing file would be " + existingFile);
		return existingFile;
	}

	public void buildModel(String source, String className, String packageName,
			String outputDirectory) throws Exception {
		JCodeModel codeModel = new JCodeModel();
		System.out.println("Attempting to build POJOs for type " + className
				+ " from source string");
		JType generate = new SchemaMapper().generate(codeModel, className,
				packageName, source);
		File f = new File(outputDirectory);
		if (f.isDirectory()) {
			File existingFile = new File(getExistingFileURL(
					f.getAbsolutePath(), packageName, className));
			if (existingFile.exists()) {
				System.out.println("Skipping " + existingFile.getAbsolutePath()
						+ " because it already exists.");
			} else {
				System.out.println("No existing file found at "
						+ existingFile.getAbsolutePath());
				System.out.println("Building model at " + f.getAbsolutePath());
				codeModel.build(f);
			}
			System.out.println("done");
		} else {
			System.out.println("ERROR: Path must be an existing directory");
		}
	}
}
