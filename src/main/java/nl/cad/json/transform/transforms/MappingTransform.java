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
package nl.cad.json.transform.transforms;

import java.util.ArrayList;
import java.util.List;

import nl.cad.json.transform.mapping.TransformSelect;
import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.visitor.AbstractVisitor;
import nl.cad.json.transform.visitor.impl.MappingVisitor;

/**
 * Allows easy mapping of properties.
 */
public class MappingTransform extends AbstractVisitor implements Transform, ValuePathTransform {

    private List<TransformSelect> transformSelects;

    public MappingTransform(List<TransformSelect> transformSelects) {
        this.transformSelects = transformSelects;
    }

    @Override
    public Object apply(Object source) {
        MappingVisitor visitor = new MappingVisitor(transformSelects);
        return visit(source, visitor);
    }

    @Override
    public void apply(ValuePath source, ValuePath target) {
        MappingVisitor visitor = new MappingVisitor(transformSelects);
        Object result = visit(source.get(), visitor);
        target.set(result);
    }

    public List<TransformSelect> getTransformSelects() {
        return new ArrayList<>(transformSelects);
    }

}
