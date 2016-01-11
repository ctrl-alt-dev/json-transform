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
package nl.ctrlaltdev.json.transform.template.handler;

import java.util.Map;

import nl.ctrlaltdev.json.transform.path.Path;
import nl.ctrlaltdev.json.transform.template.CallbackHandler;

public class PathHandler implements CallbackHandler {

    @Override
    public boolean supports(Object value) {
        return String.valueOf(value).startsWith("path:");
    }

    @Override
    public Object handle(Path path, Object value, Map<String, Object> source) {
        Path read = Path.fromString(String.valueOf(value).substring(5));
        return read.get(source);
    }

}
