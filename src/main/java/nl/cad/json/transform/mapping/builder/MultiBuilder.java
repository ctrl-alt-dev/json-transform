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
package nl.cad.json.transform.mapping.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.cad.json.transform.mapping.MoveTransformSelect;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.transforms.IdentityTransform;
import nl.cad.json.transform.transforms.NopTransform;
import nl.cad.json.transform.transforms.Transform;

public class MultiBuilder implements MultiMoveBuilder, MultiSelectBuilder {

    private List<Path> paths;
    private List<Select> selects;
    private Transform transform;

    public MultiBuilder() {
        this.paths = new ArrayList<Path>();
        this.selects = new ArrayList<Select>();
    }

    @Override
    public MultiMoveBuilder move(Path... path) {
        paths.addAll(Arrays.asList(path));
        return this;
    }

    @Override
    public MultiSelectBuilder select(Select... select) {
        selects.addAll(Arrays.asList(select));
        return this;
    }

    @Override
    public MultiSelectBuilder transform(Transform transform) {
        this.transform = transform;
        return this;
    }

    @Override
    public MultiSelectBuilder identity() {
        return transform(new IdentityTransform());
    }

    @Override
    public MultiSelectBuilder nop() {
        return transform(new NopTransform());
    }

    @Override
    public Transform build() {
        return new MoveTransformSelect(paths, transform, selects);
    }

}
