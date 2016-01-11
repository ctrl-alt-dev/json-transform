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

import nl.ctrlaltdev.json.transform.path.ValuePath;
import nl.ctrlaltdev.json.transform.select.Select;
import nl.ctrlaltdev.json.transform.select.selector.Selector;

public class SubselectSelector implements Selector {

    private Select subselect;

    public SubselectSelector(Select subselect) {
        this.subselect = subselect;
    }

    @Override
    public boolean matches(ValuePath path) {
        return subselect.select(path.value()).size() > 0;
    }

}
