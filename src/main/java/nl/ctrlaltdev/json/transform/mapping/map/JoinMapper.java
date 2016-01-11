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
package nl.ctrlaltdev.json.transform.mapping.map;

import java.util.Map;

import nl.ctrlaltdev.json.transform.mapping.source.DocumentSource;
import nl.ctrlaltdev.json.transform.merge.MergeStrategy;
import nl.ctrlaltdev.json.transform.util.NodeUtils;

/**
 * Joins a series of document sources into one document.
 */
public class JoinMapper implements DocumentSource {

    private MergeStrategy merge;
    private DocumentSource[] sources;

    public JoinMapper(MergeStrategy merge, DocumentSource... sources) {
        this.merge = merge;
        this.sources = sources;
    }

    @Override
    public Object getDocument(DocumentSource input) {
        Map<String, Object> result = NodeUtils.newObject();
        for (DocumentSource s : sources) {
            merge.merge(s.getDocument(input), result);
        }
        return result;
    }
}
