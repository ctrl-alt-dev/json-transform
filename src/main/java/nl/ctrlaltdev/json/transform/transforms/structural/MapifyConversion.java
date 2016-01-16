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
package nl.ctrlaltdev.json.transform.transforms.structural;

import java.util.List;
import java.util.Map;

import nl.ctrlaltdev.json.transform.path.ValuePath;
import nl.ctrlaltdev.json.transform.transforms.ValuePathTransform;
import nl.ctrlaltdev.json.transform.util.NodeUtils;

/**
 * Converts a collection into a map using the given property as the map key.
 */
public class MapifyConversion implements ValuePathTransform {

    private String keyProperty;

    public MapifyConversion(String keyProperty) {
        this.keyProperty = keyProperty;
    }

    @Override
    public void apply(ValuePath source, ValuePath target) {
        List<Object> array = NodeUtils.toArray(source.get());
        Map<String, Object> dest = NodeUtils.newObject();
        for (Object value : array) {
            Map<String,Object> obj = NodeUtils.toObject(value);
            dest.put(String.valueOf(obj.get(keyProperty)), obj);
        }
        target.set(dest);
    }

}
