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
package nl.cad.json.transform.select.selector;

import java.util.Map;
import java.util.regex.Pattern;

import nl.cad.json.transform.path.Path;

public class ObjectPropertyMatchSelector implements Selector {

    private final String regex;

    public ObjectPropertyMatchSelector(String regex) {
        this.regex = regex;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public boolean matches(Path path, Object value) {
        if (value instanceof Map) {
            for (String prop : ((Map<String, Object>) value).keySet()) {
                if (Pattern.matches(regex, prop)) {
                    return true;
                }
            }
        }
        return false;
    }

}
