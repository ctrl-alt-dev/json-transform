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

import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.transforms.Transform;

/**
 * takes the input document and applies the Move Transform Selects in reverse order
 * feeding the output into the input of the next.
 */
public class SequenceMapper implements DocumentSource {

    private List<Transform> ops;
    private DocumentSource src;

    public SequenceMapper(List<Transform> ops, DocumentSource src) {
        this.ops = ops;
        this.src = src;
    }

    @Override
    public Object getDocument(DocumentSource input) {
        Object source = src.getDocument(input);
        for (int t = ops.size() - 1; t >= 0; t--) {
            source = ops.get(t).apply(source);
        }
        return source;
    }
    
}
