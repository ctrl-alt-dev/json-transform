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
package nl.cad.json.transform.mapping.map;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.mapping.MoveTransformSelect;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.merge.MergeStrategy;
import nl.cad.json.transform.util.NodeUtils;

/**
 * applies the given Move-Transform-Selects to the document source
 * and merges the results into the target document.
 */
public class ParallelMapper implements DocumentSource {

    private MergeStrategy merge;
    private List<MoveTransformSelect> ops;
    private DocumentSource src;

    public ParallelMapper(MergeStrategy merge, List<MoveTransformSelect> ops, DocumentSource src) {
        this.merge = merge;
        this.ops = ops;
        this.src = src;
    }

    @Override
    public Object getDocument(DocumentSource input) {
        Map<String, Object> result = NodeUtils.newObject();
        Object source = src.getDocument(input);
        for (MoveTransformSelect op : ops) {
            merge.merge(op.map(source), result);
        }
        return result;
    }

}
