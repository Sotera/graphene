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

/**
 * Copyright (c) 2013-2014 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package graphene.model.idlhelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;

public class SerializationHelper {

	public static <T extends SpecificRecord> String toJson(T record) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Encoder encoder = EncoderFactory.get().jsonEncoder(record.getSchema(), bos);
		
		DatumWriter<T> datumWriter = new SpecificDatumWriter<T>(record.getSchema());
		datumWriter.write(record, encoder);
		encoder.flush();
		bos.flush();
		
		return bos.toString("utf-8");
	}

	public static String toJson(Object obj, Schema schema) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Encoder encoder = EncoderFactory.get().jsonEncoder(schema, bos);
		
		DatumWriter<Object> datumWriter = new SpecificDatumWriter<Object>(schema);
		datumWriter.write(obj, encoder);
		encoder.flush();
		bos.flush();
		
		return bos.toString("utf-8");
	}
	
	public static <T extends SpecificRecord> String toJson(List<T> list, Schema schema) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Encoder encoder = EncoderFactory.get().jsonEncoder(Schema.createArray(schema), bos);
		
		DatumWriter<List<T>> writer = new SpecificDatumWriter<List<T>>(Schema.createArray(schema));
		writer.write(list, encoder);
		encoder.flush();
		bos.flush();
		
		return bos.toString("utf-8");
	}

	public static <T extends SpecificRecord> String toJson(Map<String, List<T>> map, Schema schema) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Encoder encoder = EncoderFactory.get().jsonEncoder(Schema.createMap(Schema.createArray(schema)), bos);
		
		DatumWriter<Map<String, List<T>>> writer = new SpecificDatumWriter<Map<String, List<T>>>(Schema.createMap(Schema.createArray(schema)));
		writer.write(map, encoder);
		encoder.flush();
		bos.flush();
		
		return bos.toString("utf-8");
	}
	
	public static <T extends SpecificRecord> T fromJson(String json, Schema schema) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes("utf-8"));
		Decoder decoder = DecoderFactory.get().jsonDecoder(schema, bis);
		DatumReader<T> datumReader = new SpecificDatumReader<T>(schema);
		T record = null;
		record = datumReader.read(record, decoder);
		return record;
	}
	
	public static <T extends SpecificRecord> List<T> listFromJson(String json, Schema schema) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes("utf-8"));
		Decoder decoder = DecoderFactory.get().jsonDecoder(Schema.createArray(schema), bis);
		DatumReader<List<T>> datumReader = new SpecificDatumReader<List<T>>(Schema.createArray(schema));
		List<T> list = null;
		list = datumReader.read(list, decoder);
		return list;
	}
	
	public static <T extends SpecificRecord> Map<String,List<T>> mapFromJson(String json, Schema schema) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes("utf-8"));
		Decoder decoder = DecoderFactory.get().jsonDecoder(Schema.createMap(Schema.createArray(schema)), bis);
		DatumReader<Map<String,List<T>>> datumReader = new SpecificDatumReader<Map<String,List<T>>>(Schema.createMap(Schema.createArray(schema)));
		Map<String,List<T>> map = null;
		map = datumReader.read(map, decoder);
		return map;
	}
}
