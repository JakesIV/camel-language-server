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
package com.github.cameltooling.lsp.internal.instancemodel.propertiesfile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.camel.catalog.CamelCatalog;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Position;

import com.github.cameltooling.lsp.internal.completion.CamelComponentIdsCompletionsFuture;
import com.github.cameltooling.lsp.internal.instancemodel.ILineRangeDefineable;

/**
 * Represents the subpart component key.
 * For instance, with "camel.component.timer.delay=1000",
 * it is used to represents "timer"
 * 
 */
public class CamelComponentNamePropertyFileInstance implements ILineRangeDefineable {

	private String componentName;
	private CamelComponentPropertyFilekey camelComponentPropertyFilekey;
	private CompletableFuture<CamelCatalog> camelCatalog;

	public CamelComponentNamePropertyFileInstance(CompletableFuture<CamelCatalog> camelCatalog, String componentName, CamelComponentPropertyFilekey camelComponentPropertyFilekey) {
		this.camelCatalog = camelCatalog;
		this.componentName = componentName;
		this.camelComponentPropertyFilekey = camelComponentPropertyFilekey;
	}

	@Override
	public int getLine() {
		return camelComponentPropertyFilekey.getLine();
	}

	@Override
	public int getStartPositionInLine() {
		return CamelPropertyFileKeyInstance.CAMEL_COMPONENT_KEY_PREFIX.length();
	}

	@Override
	public int getEndPositionInLine() {
		return CamelPropertyFileKeyInstance.CAMEL_COMPONENT_KEY_PREFIX.length() + componentName.length();
	}

	public String getName() {
		return componentName;
	}
	
	public CompletableFuture<List<CompletionItem>> getCompletions(Position position) {
		int characterPosition = position.getCharacter();
		String componentIdBeforePosition = componentName.substring(0, characterPosition - getStartPositionInLine());
		return camelCatalog.thenApply(new CamelComponentIdsCompletionsFuture(this, componentIdBeforePosition));
	}

}
