/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.internal.generation;

import java.io.IOException;

import javax.annotation.processing.Filer;

import org.androidannotations.internal.process.ModelProcessor;

import com.helger.jcodemodel.writer.PrologCodeWriter;

public class CodeModelGenerator {

	private final Filer filer;
	private final String header;

	public CodeModelGenerator(Filer filer, String aaVersion) {
		this.filer = filer;
		this.header = "DO NOT EDIT THIS FILE.\n"
			+ "Generated using AndroidAnnotations " + aaVersion + ".\n\n"
			+ "You can create a larger work that contains this file and distribute that work under terms of your choice.\n";
	}

	public void generate(ModelProcessor.ProcessResult processResult) throws IOException {

		SourceCodeWriter sourceCodeWriter = new SourceCodeWriter(filer, processResult.originatingElements);

		PrologCodeWriter prologCodeWriter = new PrologCodeWriter(sourceCodeWriter, header);

		processResult.codeModel.build(prologCodeWriter, new ResourceCodeWriter(filer));
	}
}
