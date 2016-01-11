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

import java.util.List;

import nl.ctrlaltdev.json.transform.path.ValuePath;
import nl.ctrlaltdev.json.transform.select.selector.Selector;
import nl.ctrlaltdev.json.transform.util.NodeUtils;

public class SliceJsonPathSelector implements Selector {

    private Integer[] slice;

    public SliceJsonPathSelector(Integer[] slice) {
        this.slice = slice;
    }

    @Override
    public boolean matches(ValuePath path) {
        if (path.isRoot() || path.path().isProperty()) {
            return false;
        }
        List<Object> array = NodeUtils.toArray(path.parent().value());
        int index = array.indexOf(path.value());
        if (isSingleIndex()) {
            if (slice[0] < 0) {
                return index == array.size() + slice[0];
            } else {
                return index == slice[0];
            }
        } else if (isRangeWithStep()) {
            return index >= slice[0] && index < slice[1] && ((index - slice[0]) % slice[2] == 0);
        } else if (isRange()) {
            return index >= slice[0] && index < slice[1];
        }
        return false;
    }

    private boolean isRangeWithStep() {
        return slice[0] != null && slice[1] != null && slice[2] != null;
    }

    private boolean isRange() {
        return slice[0] != null && slice[1] != null && slice[2] == null;
    }

    private boolean isSingleIndex() {
        return slice[0] != null && slice[1] == null;
    }

}
