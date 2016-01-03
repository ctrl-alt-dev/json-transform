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
package nl.cad.json.transform.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import nl.cad.json.transform.parse.JsonParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseJson(String resource) {
        try (InputStream in = TestUtils.class.getResourceAsStream(resource)) {
            return (Map<String, Object>) new JsonParser().parse(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String renderJson(Object src) {
        try {
            return mapper.writeValueAsString(src);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

}
