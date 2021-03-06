/**
 * (C) Copyright 2011-2015 FastConnect SAS
 * (http://www.fastconnect.fr/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.fastconnect.tibco.businessworks.fcunit.processes;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.Serializable;

import org.w3c.dom.Node;

import fr.fastconnect.tibco.businessworks.fcunit.ProjectBaseDir;
import fr.fastconnect.tibco.businessworks.fcunit.resources.BWProcess;
import fr.fastconnect.tibco.businessworks.fcunit.resources.Resources;
import fr.fastconnect.tibco.businessworks.fcunit.resources.XMLTest;

public class TestedProcessFactory extends TestableProcessFactory implements Serializable {

	private static final long serialVersionUID = 2993442736047592552L;

	private final static String callProcessToTestXML = "/pd:ProcessDefinition/pd:activity[@name='callProcessToTest']/config/processNameXPath";
	private final static String callProcessToTestClassic = "/pd:ProcessDefinition/pd:activity/config/processName";

	private BWProcess[] testProcesses;
	private XMLTest[] xmlTests;

	public TestedProcessFactory() {
		super();
		testProcesses = Resources.getTestProcesses();
		xmlTests = Resources.getXMLTests();
	}

	private String getTestedProcess(File testProcess) {
		String result = null;

		Node node = Resources.getNodeFromXPath(testProcess, callProcessToTestXML);
		if (node != null) {
			result = node.getTextContent();
			result = result.substring(1, result.length() - 1);
		} else {
			node = Resources.getNodeFromXPath(testProcess, callProcessToTestClassic);
			if (node != null) {
				result = node.getTextContent();
			}
		}

		return result;

	}

	private boolean isTested(File file) {
		BWProcess process;
		try {
			process = new BWProcess(file);
		} catch (FileNotFoundException e) {
			return false;
		}

		for (int i = 0; i < testProcesses.length; i++) {
			BWProcess testProcess = testProcesses[i];

			String result = getTestedProcess(new File(ProjectBaseDir.getProjectBaseDir(), testProcess.getPath()));
			if (result != null && result.equals(process.getPath())) {
				return true;
			}
		}
		for (int i = 0; i < xmlTests.length; i++) {
			XMLTest xmlTest = xmlTests[i];

			String result = xmlTest.getTestedProcessPath();
			if (result != null && result.equals(process.getPath())) {
				return true;
			}
		}
		return false;
	}

	public class UniqueTestedProcessFilter extends TestableProcessFactory.TestableProcessFilter {

		@Override
		public boolean accept(File file) {
			return super.accept(file) && isTested(file);
		}
		
	}

	@Override
	public FileFilter getFilter() {
		return new UniqueTestedProcessFilter();
	}

}
