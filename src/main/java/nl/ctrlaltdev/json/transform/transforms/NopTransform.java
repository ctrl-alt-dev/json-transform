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

/**
 * Does nothing. Simply passes the input to the output.
 */
public class NopTransform implements Transform, ValuePathTransform {

    @Override
    public Object apply(Object source) {
        return source;
    }

    @Override
    public void apply(ValuePath source, ValuePath target) {
        target.set(source.get());
    }

}
