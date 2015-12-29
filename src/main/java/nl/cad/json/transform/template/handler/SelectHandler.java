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
package nl.cad.json.transform.template.handler;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.select.SelectBuilder;
import nl.cad.json.transform.template.CallbackHandler;
import nl.cad.json.transform.util.NodeUtils;

public class SelectHandler implements CallbackHandler {

    private static final String SELECT = "select:";
    private static final String SINGLE_SELECT = "single-select:";

    @Override
    public boolean supports(Object value) {
        return isMultiSelect(value) || isSingleSelect(value);
    }

    private boolean isSingleSelect(Object value) {
        return String.valueOf(value).startsWith(SINGLE_SELECT);
    }

    private boolean isMultiSelect(Object value) {
        return String.valueOf(value).startsWith(SELECT);
    }

    @Override
    public Object handle(Path path, Object value, Map<String, Object> source) {
        boolean singleSelect = isSingleSelect(value);
        Select read = SelectBuilder.fromJsonPath(String.valueOf(value).substring(singleSelect ? SINGLE_SELECT.length() : SELECT.length()));
        if (singleSelect) {
            return read.selectOne(source).value();
        } else {
            List<Object> results = NodeUtils.newArray();
            for (ValuePath found : read.select(source)) {
                results.add(found.value());
            }
            return results;
        }
    }

}
