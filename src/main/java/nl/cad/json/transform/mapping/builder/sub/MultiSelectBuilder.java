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
package nl.cad.json.transform.mapping.builder.sub;

import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.transforms.Transform;

/**
 * Allows the creation of multiple selects as input for the move-transform-select.
 */
public interface MultiSelectBuilder {

    /**
     * adds one or more selects.
     * @param select the selects.
     * @return the builder.
     */
    MultiSelectBuilder select(Select... select);

    /**
     * finishes this builder.
     * @return the created transform.
     */
    Transform build();

}
