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
package nl.cad.json.transform.select.jsonpath;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.select.selector.Selector;
import nl.cad.json.transform.util.NodeUtils;

public class BasicJsonPathSelector implements Selector {

    private ValueType valueType;
    private String nameValue;
    private NameType nameType;
    private Object arg;

    public BasicJsonPathSelector(NameType nameType, Object arg, ValueType valueType) {
        this.nameType = nameType;
        this.arg = arg;
        this.valueType = valueType;
    }

    @Override
    public boolean matches(ValuePath valuePath) {
        Path path = valuePath.path();
        Object value = valuePath.value();
        switch (nameType) {
        case CURRENT_NODE:
        case ROOT_NODE:
            if (!path.isRoot()) {
                return false;
            }
            break;
        case INDEX:
            if (path.isRoot()) {
                return false;
            }
            if (!path.getTop().equals(arg)) {
                return false;
            }
        case ANY_INDEX:
            if (!path.isIndex()) {
                return false;
            }
            break;
        case PROPERTY:
            if (path.isRoot()) {
                return false;
            }
            if (!path.getTop().equals(arg)) {
                return false;
            }
        case ANY_PROPERTY_OR_INDEX_NODE:
            if (!path.isProperty() && !path.isIndex()) {
                return false;
            }
            break;
        default:
            throw new IllegalArgumentException(String.valueOf(nameType));
        }
        if (valueType == null) {
            return true;
        }
        switch (valueType) {
        case ARRAY_VALUE_NODE:
            return NodeUtils.isArray(value);
        case OBJECT_VALUE_NODE:
            return NodeUtils.isObject(value);
        default:
            return true;
        }        
    }

}
