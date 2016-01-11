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
package nl.ctrlaltdev.json.transform.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import nl.ctrlaltdev.json.transform.parse.JsonTokenizer.JsonToken;
import nl.ctrlaltdev.json.transform.parse.JsonTokenizer.JsonTokenType;
import nl.ctrlaltdev.json.transform.util.NodeUtils;

/**
 * parses Json data into the Map/List/Value structure.
 */
public class JsonParser {

    public class Scanner {
        private int idx = 0;
        private List<JsonToken> tokens;

        public Scanner(List<JsonToken> tokens) {
            this.tokens = tokens;
        }

        public Scanner next() {
            idx++;
            return this;
        }

        public JsonToken get() {
            return tokens.get(idx);
        }

    }

    private JsonTokenizer tokenizer;

    public JsonParser() {
        this(new JsonTokenizer());
    }

    public JsonParser(JsonTokenizer jsonTokenizer) {
        this.tokenizer = jsonTokenizer;
    }

    public final Object parse(InputStream in) throws IOException {
        return build(tokenizer.tokenize(new InputStreamReader(in, Charset.forName("UTF-8"))));
    }

    public final Object parse(InputStream in, Charset charset) throws IOException {
        return build(tokenizer.tokenize(new InputStreamReader(in, charset)));
    }

    public final Object parse(Reader reader) throws IOException {
        return build(tokenizer.tokenize(reader));
    }

    public final Object parse(String document) {
        return build(tokenizer.tokenize(document));
    }

    private Object build(List<JsonToken> tokens) {
        return handleValue(new Scanner(tokens));
    }

    private Object handleValue(Scanner tokens) {
        switch (tokens.get().getType()) {
        case OBJECT_START:
            return handleObjectValue(tokens.next());
        case ARRAY_START:
            return handleArrayValue(tokens.next());
        case VALUE:
            Object result = tokens.get().getValue();
            tokens.next();
            return result;
        case ARRAY_END:
        case OBJECT_END:
        case NAME:
        default:
            throw new RuntimeException(String.valueOf(tokens.get()));
        }
    }

    private Object handleArrayValue(Scanner tokens) {
        List<Object> array = NodeUtils.newArray();
        while (tokens.get().getType() != JsonTokenType.ARRAY_END) {
            array.add(handleValue(tokens));
        }
        tokens.next();
        return array;
    }

    private Object handleObjectValue(Scanner tokens) {
        Map<String, Object> result = NodeUtils.newObject();
        while (tokens.get().getType() != JsonTokenType.OBJECT_END) {
            result.put(handleName(tokens), handleValue(tokens.next()));
        }
        tokens.next();
        return result;
    }

    private String handleName(Scanner next) {
        if (next.get().getType() != JsonTokenType.NAME) {
            throw new IllegalArgumentException("Expected name, got " + next.get());
        }
        return (String) next.get().getValue();
    }

}
