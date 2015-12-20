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
package nl.cad.json.transform.template;

import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.transforms.IdentityTransform;
import nl.cad.json.transform.visitor.AbstractVisitor;

/**
 * A Callback Template takes a given JSON as a template and calls a
 * callback handler for its value nodes, if the callback handler supports
 * that value. This allows dynamic replacement of those values in
 * the document. If the value is unsupported, the original value remains.
 */
public class CallbackTemplate extends AbstractVisitor implements Template {

    private Map<String, Object> template;

    private IdentityTransform idTransform;

    private CallbackHandler handler;

    public CallbackTemplate(Map<String, Object> template, CallbackHandler handler) {
        this.template = template;
        this.handler = handler;
        this.idTransform = new IdentityTransform();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> fill(Map<String, Object> source) {

        Map<String, Object> target = (Map<String, Object>) idTransform.apply(Path.root(), template);

        visit(target, new VisitorImpl() {

            @Override
            public void onValue(Path path, Object object) {
                if (handler.supports(object)) {
                    Object result = handler.handle(path, object, source);
                    path.set(target, result);
                }
            }

        });
        return target;
    }

}
