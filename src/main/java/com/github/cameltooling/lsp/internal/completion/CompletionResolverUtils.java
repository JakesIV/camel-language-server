/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cameltooling.lsp.internal.completion;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.parser.helper.CamelXmlHelper;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.TextEdit;
import org.w3c.dom.Node;

import com.github.cameltooling.lsp.internal.instancemodel.CamelURIInstance;
import com.github.cameltooling.lsp.internal.instancemodel.ILineRangeDefineable;
import com.github.cameltooling.lsp.internal.instancemodel.ReferenceUtils;
import com.github.cameltooling.lsp.internal.parser.ParserXMLFileHelper;

/**
 * @author lheinema
 */
public class CompletionResolverUtils {
	
	private CompletionResolverUtils() {
		// util class
	}
	
	public static void applyTextEditToCompletionItem(ILineRangeDefineable lineRangeDefineable, CompletionItem item) {
		if (lineRangeDefineable != null) {
			int line = lineRangeDefineable.getLine();
			Position pStart = new Position(line, lineRangeDefineable.getStartPositionInLine());
			Position pEnd = new Position(line, lineRangeDefineable.getEndPositionInLine());
			
			Range range = new Range(pStart, pEnd);
			
			// replace instead of insert
			if(item.getInsertText() != null) {
				item.setTextEdit(new TextEdit(range, item.getInsertText()));
			} else {
				item.setTextEdit(new TextEdit(range, item.getLabel()));
			}
		}
	}

	public static List<String> retrieveEndpointIDsOfScheme(String scheme, ParserXMLFileHelper xmlFileHelper, TextDocumentItem docItem) throws Exception {
		List<Node> allEndpoints = xmlFileHelper.getAllEndpoints(docItem);
		List<String> endpointIDs = new ArrayList<>();
		for (Node endpoint : allEndpoints) {
			String uriToParse = CamelXmlHelper.getSafeAttribute(endpoint, "uri");
			if (uriToParse != null) {
				CamelURIInstance uriInstance = new CamelURIInstance(uriToParse, endpoint, docItem);
				if (ReferenceUtils.isReferenceComponentKind(uriInstance) && uriInstance.getComponentName().equalsIgnoreCase(scheme)) {
					String dId = ReferenceUtils.getReferenceKey(uriInstance);
					String directValue = String.format("%s:%s", scheme, dId);
					if (dId != null && dId.trim().length()>0 && !endpointIDs.contains(directValue)) {
						endpointIDs.add(directValue);
					}
				}
			}
		}
		return endpointIDs;
	}

}
