/*
 * Copyright 2015 E.Hooijmeijer / www.ctrl-alt-dev.nl
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
package nl.cad.json.transform.mapping.builder;

import java.util.Map;
import java.util.TreeMap;

import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.SplitSource;
import nl.cad.json.transform.select.Select;

public class SplitSourceBuilder {

    private DocumentSource source;
    private Map<String, Select> selects;

    protected SplitSourceBuilder(DocumentSource source) {
        this.source = source;
        this.selects = new TreeMap<String, Select>();
    }

    public SplitSourceBuilder add(String name, Select select) {
        selects.put(name, select);
        return this;
    }

    public SplitSource build() {
        return new SplitSource(source, selects);
    }

}
