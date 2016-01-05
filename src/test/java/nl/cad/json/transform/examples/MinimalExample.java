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

import nl.cad.json.transform.JsonTransform;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.ValueSource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class MinimalExample {

    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
        //
        String document = "{ \"some\":\"value\" }";
        //
        // Build the mapping.
        //
        DocumentSource mapping = JsonTransform.sequence(
                JsonTransform.move(JsonTransform.path("elsewhere"))
                ).build();
        //
        // Pull the output from the input.
        //
        Object output = mapping.getDocument(
                new ValueSource(JsonTransform.parse(document))
                );
        //
        // Show the output.
        //
        System.out.println(JsonTransform.print(output));
        //
        // {"elsewhere":{"some":"value"}}
        //
    }

}
