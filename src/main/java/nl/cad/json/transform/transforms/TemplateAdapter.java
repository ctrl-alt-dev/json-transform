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
package nl.cad.json.transform.transforms;

import java.util.Map;

import nl.cad.json.transform.template.Template;
import nl.cad.json.transform.util.NodeUtils;

/**
 * Allows you to use a {@link Template} as {@link Transform}.
 */
public class TemplateAdapter implements Transform {

    private Template template;

    public TemplateAdapter(Template template) {
        this.template = template;
    }

    @Override
    public Map<String, Object> apply(Object source) {
        return template.fill(NodeUtils.toObject(source));
    }

}
