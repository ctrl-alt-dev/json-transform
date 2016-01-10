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

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.transforms.Transform;

/**
 * The multi-move builder is used for move-transform-selects
 * that have multiple selects into the same transform and/or
 * multiple target paths as output.
 */
public interface MultiMoveBuilder {

    /**
     * adds another destination path to the builder.
     * @param path the path.
     * @return the builder.
     */
    MultiMoveBuilder move(Path... path);
    
    /**
     * adds a transform to the builder.
     * @param transform the transform.
     * @return the select builder.
     */
    MultiSelectBuilder transform(Transform transform);

    /**
     * adds an identity transform to the builder.
     * @return the select builder.
     */
    MultiSelectBuilder identity();

    /**
     * adds a no-operation transform to the builder.
     * @return the select builder.
     */
    MultiSelectBuilder nop();
}
