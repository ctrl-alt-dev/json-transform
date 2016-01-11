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
package nl.ctrlaltdev.json.transform.mapping;

import nl.ctrlaltdev.json.transform.path.ValuePath;
import nl.ctrlaltdev.json.transform.select.Select;
import nl.ctrlaltdev.json.transform.transforms.MappingTransform;
import nl.ctrlaltdev.json.transform.transforms.ValuePathTransform;

/**
 * The select of the Transform select is tested against each node and if
 * it matches, the transform is executed. For arrays and objects the
 * select is tested by default at the start of the node, however for
 * some purposes its best to test at the end of the node. The post
 * boolean makes the transform select execute at the end of the node.
 * 
 * Works with the {@link MappingTransform}.
 */
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
