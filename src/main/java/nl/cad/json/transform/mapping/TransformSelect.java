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
package nl.cad.json.transform.mapping;

import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.transforms.ValuePathTransform;

public class TransformSelect {

    private ValuePathTransform transform;
    private Select select;
    private boolean post;

    public TransformSelect(ValuePathTransform transform, Select select) {
        this(transform, select, false);
    }

    public TransformSelect(ValuePathTransform transform, Select select, boolean post) {
        this.transform = transform;
        this.select = select;
        this.post = post;
    }

    public boolean isPost() {
        return post;
    }

    public boolean isMatch(ValuePath path) {
        return select.isMatch(path);
    }

    public void apply(ValuePath source, ValuePath target) {
        transform.apply(source, target);
    }

}
