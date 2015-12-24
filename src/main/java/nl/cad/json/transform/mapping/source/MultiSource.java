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
package nl.cad.json.transform.mapping.source;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * container document source for named (and Future) documents.
 * Use this together with the {@link NamedSource}.
 */
public final class MultiSource implements DocumentSource, CompositeSource {

    public class FutureDocumentException extends RuntimeException {
        public FutureDocumentException(Exception ex) {
            super(ex);
        }
    }

    public class UndefinedSourceNameException extends RuntimeException {
        public UndefinedSourceNameException(String name) {
            super(name + " in " + sources.keySet().toString());
        }
    }

    private Map<String, Object> sources = new TreeMap<String, Object>();

    public MultiSource() {
    }

    public MultiSource(Map<String, Object> src) {
        sources = new TreeMap<String, Object>(src);
    }

    public void putDocument(String name, Object document) {
        sources.put(name, document);
    }

    public void putFutureDocument(String name, Future<Object> document) {
        sources.put(name, document);
    }

    public void putSource(String name, DocumentSource document) {
        sources.put(name, document);
    }

    @Override
    public Object getDocument(DocumentSource input) {
        return input.getDocument(null);
    }

    @SuppressWarnings("unchecked")
    public Object getDocument(String name) {
        if (sources.containsKey(name)) {
            Object doc = sources.get(name);
            if (doc instanceof Future) {
                try {
                    doc = ((Future<Object>) doc).get();
                } catch (InterruptedException | ExecutionException ex) {
                    throw new FutureDocumentException(ex);
                }
            } else if (doc instanceof DocumentSource) {
                doc = ((DocumentSource) doc).getDocument(null);
            }
            return doc;
        }
        throw new UndefinedSourceNameException(name);
    }

}
