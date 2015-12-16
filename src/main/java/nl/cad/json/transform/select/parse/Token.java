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
package nl.cad.json.transform.select.parse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nl.cad.json.transform.select.SelectBuilder;

public class Token {

    private String method;
    private List<String> args = new ArrayList<>();

    public void setMethod(String method) {
        this.method = method;
    }

    public void addArg(String arg) {
        args.add(arg);
    }

    public boolean isEmpty() {
        return method == null;
    }

    public void invoke(SelectBuilder builder) {
        Method m;
        try {
            m = SelectBuilder.class.getDeclaredMethod(method, paramTypes());
            m.setAccessible(true);
            m.invoke(builder, toArgs());
        } catch (ReflectiveOperationException e) {
            throw new TokenException(this, e);
        } catch (SecurityException e) {
            throw new TokenException(this, e);
        }
    }

    private Object[] toArgs() {
        return args.toArray(new Object[args.size()]);
    }

    @SuppressWarnings("rawtypes")
    private Class[] paramTypes() {
        List<Class> results = new ArrayList<Class>();
        for (int t = 0; t < args.size(); t++) {
            results.add(String.class);
        }
        return results.toArray(new Class[results.size()]);
    }

    @Override
    public String toString() {
        return String.valueOf(method) + String.valueOf(args);
    }

}
