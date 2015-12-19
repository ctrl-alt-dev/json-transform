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
package nl.cad.json.transform.java.pojo.serializers;

import java.util.List;

import nl.cad.json.transform.java.pojo.ToDocumentMapper;

public class ValueSerializer implements Serializer {
    
    @Override
    public boolean supports(Object value) {
        return value instanceof Number ||
                value instanceof Boolean ||
                value instanceof String ||
                value instanceof Character;
    }
    
    @Override
    public Object toDocument(ToDocumentMapper mapper, List<Object> stack, Object value) {
        return value;
    }

}
