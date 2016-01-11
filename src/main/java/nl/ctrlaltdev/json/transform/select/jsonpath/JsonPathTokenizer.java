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
package nl.ctrlaltdev.json.transform.select.jsonpath;

import java.util.ArrayList;
import java.util.List;

public class JsonPathTokenizer {

    public static class JsonPathParseException extends RuntimeException {
        public JsonPathParseException(String msg, String str, int pos) {
            super(msg + " in '" + str + "' at position " + pos);
        }
    }

    public static class JsonPathToken {

        private NameType nameType;
        private Object argument;
        private ValueType valueType;

        public JsonPathToken(NameType nameType) {
            this(nameType, null);
        }

        public JsonPathToken(NameType nameType, Object arg) {
            this.nameType = nameType;
            this.argument = arg;
        }

        public JsonPathToken(JsonPathToken name, ValueType type) {
            if (name == null) {
                throw new JsonPathParseException("Missing name part.", "", -1);
            }
            this.nameType = name.nameType;
            this.argument = name.argument;
            this.valueType = type;
        }

        public NameType getNameType() {
            return nameType;
        }

        public ValueType getValueType() {
            return valueType;
        }

        public Object getArgument() {
            return argument;
        }

        @Override
        public String toString() {
            return nameType + "(" + argument + ")-" + valueType;
        }
    }

    public static List<JsonPathToken> tokenize(String str) {
        int inIndex = 0;
        StringBuilder token = new StringBuilder();
        JsonPathToken current = null;
        List<JsonPathToken> results = new ArrayList<JsonPathToken>();
        for (int t = 0; t < str.length(); t++) {
            char curr = str.charAt(t);
            if (curr == '$' && inIndex == 0) {
                current = new JsonPathToken(NameType.ROOT_NODE);
            } else if (curr == '@' && inIndex == 0) {
                current = new JsonPathToken(NameType.CURRENT_NODE);
            } else if (curr == '.' && inIndex == 0) {
                current = endToken(current, token);
                if (current == null) {
                    results.add(new JsonPathToken(new JsonPathToken(NameType.NESTED_NODES), ValueType.ANY_VALUE_NODE));
                } else {
                    results.add(new JsonPathToken(current, ValueType.OBJECT_VALUE_NODE));
                }
                current = null;
            } else if (curr == '[') {
                if (inIndex == 0) {
                    current = endToken(current, token);
                    results.add(new JsonPathToken(current, ValueType.ARRAY_VALUE_NODE));
                    current = null;
                }
                token.append(curr);
                inIndex++;
            } else if (curr == ']') {
                token.append(curr);
                inIndex--;
                if (inIndex == 0) {
                    current = endToken(current, token);
                }
            } else if (curr == '<' || curr == '=' || curr == '>') {
                throw new JsonPathParseException("JsonPath Expressions are unsupported", str, t);
            } else {
                token.append(curr);
            }
        }
        if (inIndex > 0) {
            throw new JsonPathParseException("Missing ] ", str, str.length());
        }
        current = endToken(current, token);
        if (current != null) {
            results.add(new JsonPathToken(current, ValueType.ANY_VALUE_NODE));
        }
        return results;
    }

    private static JsonPathToken endToken(JsonPathToken current, StringBuilder token) {
        JsonPathToken result = current;
        if (token.length() > 0) {
            result = parseToken(token.toString());
            token.delete(0, token.length());
        }
        return result;
    }

    private static JsonPathToken parseToken(String string) {
        if (string.startsWith("[") && string.endsWith("]")) {
            return parseBracketedExpression(string.substring(1, string.length() - 1));
        }
        return parseProperty(string);
    }

    private static JsonPathToken parseBracketedExpression(String string) {
        if (string.startsWith("'") && string.endsWith("'")) {
            return parseProperty(string.substring(1, string.length() - 1));
        } else if (string.startsWith("?")) {
            return parseBooleanExpression(string.substring(1));
        } else if (string.startsWith("(") && string.endsWith(")")) {
            return parseEvalExpression(string.substring(1, string.length() - 1), false);
        } else if (string.indexOf(':') >= 0) {
            return parseSlice(string);
        } else if (string.equals("*")) {
            return new JsonPathToken(NameType.ANY_INDEX);
        } else {
            return new JsonPathToken(NameType.INDEX, Integer.parseInt(string));
        }
    }

    private static JsonPathToken parseEvalExpression(String str, boolean filter) {
        if (!filter) {
            throw new JsonPathParseException("Only filter expressions are supported", str, -1);
        }
        return new JsonPathToken(NameType.EXPRESSION, tokenize(str));
    }

    private static JsonPathToken parseBooleanExpression(String string) {
        if (string.startsWith("(") && string.endsWith(")")) {
            return parseEvalExpression(string.substring(1, string.length() - 1), true);
        } else {
            throw new JsonPathParseException("A filter expression must have brackets.", string, -1);
        }
    }

    private static JsonPathToken parseSlice(String string) {
        List<Integer> results = new ArrayList<Integer>();
        StringBuilder sb = new StringBuilder();
        for (int t = 0; t < string.length(); t++) {
            if (string.charAt(t) == ':') {
                if (sb.length() > 0) {
                    results.add(Integer.parseInt(sb.toString()));
                } else {
                    results.add(null);
                }
                sb.delete(0, sb.length());
            } else {
                sb.append(string.charAt(t));
            }
        }
        while (results.size() < 3) {
            results.add(null);
        }
        return new JsonPathToken(NameType.SLICE, results);
    }

    private static JsonPathToken parseProperty(String property) {
        if ("*".equals(property)) {
            return new JsonPathToken(NameType.ANY_PROPERTY_OR_INDEX_NODE);
        }
        return new JsonPathToken(NameType.PROPERTY, property);
    }

}
