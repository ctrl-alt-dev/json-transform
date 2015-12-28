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
package nl.cad.json.transform.select.selector;

import nl.cad.json.transform.path.ValuePath;

public class ExorSelector implements Selector {

    private final Selector[] selectors;

    public ExorSelector(Selector... selectors) {
        this.selectors = selectors;
    }

    @Override
    public boolean matches(ValuePath path) {
        int cnt = 0;
        for (Selector sel : selectors) {
            if (sel.matches(path)) {
                cnt++;
                if (cnt > 1) {
                    return false;
                }
            }
        }
        return cnt == 1;
    }

}
