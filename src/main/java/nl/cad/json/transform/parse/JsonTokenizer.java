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
package nl.cad.json.transform.parse;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;




public class JsonTokenizer {

    public static enum JsonTokenType {
        NAME, VALUE, ARRAY_START, ARRAY_END, OBJECT_START, OBJECT_END;

        public boolean matches(JsonTokenType type) {
            switch (this) {
            case ARRAY_START:
                return type == ARRAY_END;
            case OBJECT_START:
                return type == OBJECT_END;
            default:
                return false;
            }
        }
    }
    
    public static class JsonToken {
        private final JsonTokenType type;
        private final Object value;

        public JsonToken(JsonTokenType type) {
            this(type, null);
        }

        public JsonToken(JsonTokenType type, Object value) {
            this.type = type;
            this.value = value;
        }

        public JsonTokenType getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "(" + type + ":" + value + ")";
        }
    }

    public static class ParserException extends RuntimeException {
        public ParserException(int pos) {
            this("Invalid token", pos);
        }

        public ParserException(String msg, int pos) {
            super(msg + " at " + String.valueOf(pos));
        }
    }

    public List<JsonToken> tokenize(String document) {
        try {
            return tokenize(new StringReader(document));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<JsonToken> tokenize(Reader document) throws IOException {
        List<JsonTokenType> stack = new ArrayList<JsonTokenizer.JsonTokenType>();
        List<JsonToken> tokens = new ArrayList<JsonToken>();
        StringBuilder current = new StringBuilder();
        StringBuilder escape = new StringBuilder();
        boolean inQuote = false;
        boolean inEscape = false;
        boolean wasQuoted = false;
        boolean expectName = false;
        int read = -1;
        int t = 0;
        while ((read = document.read()) != -1) {
            t++;
            char c = (char)read;
            if (inQuote) {
                if (inEscape) {
                    escape.append(c);
                    inEscape = handleEscape(escape, current, t);
                } else if (c == '\\') {
                    inEscape = true;
                    escape.delete(0, escape.length());
                } else if (c == '"') {
                    inQuote = false;
                    wasQuoted = true;
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                inQuote = true;
            } else if (c == '[') {
                wasQuoted = endToken(tokens, current, wasQuoted, expectName, t);
                expectName = begin(tokens, stack, JsonTokenType.ARRAY_START);
            } else if (c == ']') {
                wasQuoted = endToken(tokens, current, wasQuoted, expectName, t);
                expectName = end(tokens, stack, JsonTokenType.ARRAY_END, t);
            } else if (c == '{') {
                wasQuoted = endToken(tokens, current, wasQuoted, expectName, t);
                expectName = begin(tokens, stack, JsonTokenType.OBJECT_START);
            } else if (c == '}') {
                wasQuoted = endToken(tokens, current, wasQuoted, expectName, t);
                expectName = end(tokens, stack, JsonTokenType.OBJECT_END, t);
            } else if (c == ',') {
                wasQuoted = endToken(tokens, current, wasQuoted, expectName, t);
                expectName = expectName(stack);
            } else if (c == ':') {
                wasQuoted = endToken(tokens, current, wasQuoted, expectName, t);
                expectName = false;
            } else if (!isSkip(c)) {
                current.append(c);
            }
        }
        endToken(tokens, current, wasQuoted, expectName, t);
        return tokens;
    }

    private boolean begin(List<JsonToken> tokens, List<JsonTokenType> stack, JsonTokenType type) {
        tokens.add(new JsonToken(type));
        stack.add(type);
        return type == JsonTokenType.OBJECT_START;
    }

    private boolean end(List<JsonToken> tokens, List<JsonTokenType> stack, JsonTokenType type, int pos) {
        tokens.add(new JsonToken(type));
        JsonTokenType top = stack.remove(stack.size() - 1);
        if (!top.matches(type)) {
            throw new ParserException("Expected " + top + " token ", pos);
        }
        return expectName(stack);
    }

    private boolean expectName(List<JsonTokenType> stack) {
        if (!stack.isEmpty()) {
            return stack.get(stack.size() - 1) == JsonTokenType.OBJECT_START;
        } else {
            return false;
        }
    }

    private boolean isSkip(char c) {
        return Character.isWhitespace(c) || c == '\n' || c == '\r' || c == '\t' || c == '\b' || c == '\f';
    }

    private boolean endToken(List<JsonToken> tz, StringBuilder current, boolean quoted, boolean name, int pos) {
        if (current.length() != 0) {
            String value = current.toString();
            if (quoted) {
                tz.add(new JsonToken(name ? JsonTokenType.NAME : JsonTokenType.VALUE, value));
            } else if (name) {
                throw new ParserException("Expected a name, not a value", pos);
            } else if ("true".equals(value)) {
                tz.add(new JsonToken(JsonTokenType.VALUE, Boolean.TRUE));
            } else if ("false".equals(value)) {
                tz.add(new JsonToken(JsonTokenType.VALUE, Boolean.FALSE));
            } else if ("null".equals(value)) {
                tz.add(new JsonToken(JsonTokenType.VALUE, null));
            } else {
                try {
                    tz.add(new JsonToken(JsonTokenType.VALUE, parseNumber(value)));
                } catch (NumberFormatException ex) {
                    throw new ParserException("Unknown value '" + value + "'", pos);
                }
            }
            current.delete(0, current.length());
        }
        return false;
    }

    protected Number parseNumber(String value) {
        if (value.indexOf('.') > 0) {
            return Double.parseDouble(value);
        } else if (value.length() > 10) {
            return Long.parseLong(value);
        } else {
            return Integer.parseInt(value);
        }
    }

    private boolean handleEscape(StringBuilder escape, StringBuilder sb, int pos) {
        switch (escape.charAt(0)) {
        case 'b':
            sb.append("\b");
            return false;
        case 'f':
            sb.append("\f");
            return false;
        case 'n':
            sb.append("\n");
            return false;
        case 'r':
            sb.append("\r");
            return false;
        case 't':
            sb.append("\t");
            return true;
        case 'u':
            if (escape.length() == 5) {
                int c = Integer.parseInt(escape.substring(1, 5), 16);
                sb.append((char) c);
                return false;
            }
            return true;
        case '/':
            sb.append("/");
            return false;
        case '\\':
            sb.append("\\");
            return false;
        case '"':
            sb.append("\"");
            return false;
        default:
            throw new ParserException(pos);
        }
    }

}
