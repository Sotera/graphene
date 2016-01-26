/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.model;

import java.io.File;
import java.net.URL;

import org.jsonschema2pojo.SchemaMapper;

import com.sun.codemodel.JCodeModel;

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
	private boolean overwrite = false;

	public GenerateModel(final boolean overwrite) {
		this.overwrite = overwrite;
	}

	public void buildModel(final String source, final String className,
			final String packageName, final String outputDirectory)
			throws Exception {
		final JCodeModel codeModel = new JCodeModel();

		System.out.println("Attempting to build POJOs for type " + className
				+ " from source string");
		new SchemaMapper().generate(codeModel, className, packageName, source);
		final File f = new File(outputDirectory);
		if (f.isDirectory()) {
			final File existingFile = new File(getExistingFileURL(
					f.getAbsolutePath(), packageName, className));
			if (!overwrite && existingFile.exists()) {
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

	public void buildModel(final URL source, final String className,
			final String packageName, final String outputDirectory)
			throws Exception {
		final JCodeModel codeModel = new JCodeModel();
		System.out.println("Attempting to build POJOs for type " + className
				+ " from source " + source.getPath());

		new SchemaMapper().generate(codeModel, className, packageName, source);
		final File f = new File(outputDirectory);
		if (f.isDirectory()) {
			final File existingFile = new File(getExistingFileURL(
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

	private String getExistingFileURL(final String outputDirectory,
			final String packageName, final String className) {
		final String packagePath = packageName.replaceAll("[.]", "//");
		final String existingFile = outputDirectory + "//" + packagePath + "//"
				+ className + ".java";
		System.out
				.println("Potentially existing file would be " + existingFile);
		return existingFile;
	}
}
