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
package nl.cad.json.transform.print;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.SortedMap;

import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.visitor.AbstractVisitor;

public class JsonPrinter extends AbstractVisitor {

    public String toString(Object obj) {
        StringWriter writer = new StringWriter();
        write(obj, writer, false);
        return writer.toString();
    }

    public String toPrettyString(Object obj) {
        StringWriter writer = new StringWriter();
        write(obj, writer, true);
        return writer.toString();
    }

    public void write(Object obj, final Writer writer, final boolean pretty) {
        visit(obj, new ValuePathVisitor() {
            private int level = 0;
            private boolean first = true;
            @Override
            public void onValue(ValuePath source, ValuePath target) {
                try {
                    before(writer, pretty, source);
                    Object value = source.get();
                    if (value instanceof String) {
                        renderString(writer, (String) value);
                    } else {
                        writer.append(value.toString());
                    }
                    after(writer, source);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void onEndObject(ValuePath source, ValuePath target) {
                try {
                    level--;
                    if (pretty) {
                        indent(writer);
                    }
                    writer.append("}");
                    after(writer, source);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void onEndArray(ValuePath source, ValuePath target) {
                try {
                    level--;
                    if (pretty) {
                        indent(writer);
                    }
                    writer.append("]");
                    after(writer, source);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }

            @Override
            public boolean onBeginObject(ValuePath source, ValuePath target) {
                try {
                    before(writer, pretty, source);
                    writer.append("{");
                    level++;
                    return true;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public boolean onBeginArray(ValuePath source, ValuePath target) {
                try {
                    before(writer, pretty, source);
                    writer.append("[");
                    level++;
                    return true;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            private void before(final Writer writer, final boolean pretty, ValuePath source) throws IOException {
                if (pretty) {
                    indent(writer);
                }
                if (source.path().isProperty()) {
                    renderString(writer, (String) source.path().getTop());
                    writer.append(":");
                } else if (source.path().isIndex()) {
                    if (!isFirst(source)) {
                        writer.append(",");
                    }
                }
            }

            private void after(final Writer writer, ValuePath source) throws IOException {
                if (source.path().isProperty()) {
                    if (!isLast(source)) {
                        writer.append(",");
                    }
                }
            }

            protected void indent(Writer writer) throws IOException {
                if (!first) {
                    writer.append(System.getProperty("line.separator"));
                }
                first = false;
                for (int t = 0; t < level; t++) {
                    writer.append("  ");
                }
            }

        });
    }

    @SuppressWarnings("unchecked")
    protected boolean isLast(ValuePath source) {
        String name = (String) source.path().getTop();
        SortedMap<String, Object> obj = (SortedMap<String, Object>) source.parent().get();
        return obj.lastKey().equals(name);
    }

    @SuppressWarnings("unchecked")
    protected boolean isFirst(ValuePath source) {
        Object value = source.get();
        List<Object> arr = (List<Object>) source.parent().get();
        return arr.get(0) == value;
    }

    protected void renderString(Writer sb, String value) throws IOException {
        sb.append("\"");
        for (int t = 0; t < value.length(); t++) {
            char c = value.charAt(t);
            if (c == '"' || c == '\\' || c == '/') {
                sb.append("\\");
                sb.append(c);
            } else if (c < 32) {
                switch (c) {
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    StringBuilder str = new StringBuilder(Integer.toHexString((int) c));
                    while (str.length() < 4) {
                        str.insert(0, "0");
                    }
                    sb.append("\\u");
                    sb.append(str);
                    break;
                }
            } else {
                sb.append(c);
            }
        }
        sb.append("\"");
    }

}
