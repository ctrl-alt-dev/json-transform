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
package nl.ctrlaltdev.json.transform.transforms;

import nl.ctrlaltdev.json.transform.path.ValuePath;

public class ValuePathTransformAdapter implements Transform {

    private final ValuePathTransform vpt;

    public ValuePathTransformAdapter(ValuePathTransform vpt) {
        this.vpt = vpt;
    }

    @Override
    public Object apply(Object value) {
        ValuePath source = new ValuePath(value);
        ValuePath target = new ValuePath(null);
        vpt.apply(source, target);
        return target.get();
    }

}
