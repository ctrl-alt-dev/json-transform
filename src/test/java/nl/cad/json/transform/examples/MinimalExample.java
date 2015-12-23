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
package nl.cad.json.transform.examples;

import java.io.IOException;
import java.util.Map;

import nl.cad.json.transform.mapping.builder.MappingBuilder;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.ValueSource;
import nl.cad.json.transform.path.Path;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MinimalExample {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
        //
        ObjectMapper objectMapper = new ObjectMapper();
        //
        String document = "{ \"some\":\"value\" }";
        //
        // Load JSON Document using Jackson into its basic Java form.
        //
        Map<String, Object> input = (Map<String, Object>) objectMapper.readValue(document, Map.class);
        //
        // Build the mapping.
        //
        DocumentSource mapping = MappingBuilder.seq().move(Path.fromString("elsewhere")).build();
        //
        // Pull the output from the input.
        //
        Object output = mapping.getDocument(new ValueSource(input));
        //
        // Show the output.
        //
        System.out.println(objectMapper.writeValueAsString(output));
        //
        // {"elsewhere":{"some":"value"}}
        //
    }

}
