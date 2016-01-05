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
import java.util.List;

import nl.cad.json.transform.mapping.TransformSelect;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.path.relative.RelativePath;
import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.select.SelectBuilder;
import nl.cad.json.transform.transforms.MappingTransform;
import nl.cad.json.transform.transforms.ValuePathTransform;
import nl.cad.json.transform.transforms.convert.AbsoluteMovePropertyConversion;
import nl.cad.json.transform.transforms.convert.AddPropertyConversion;
import nl.cad.json.transform.transforms.convert.DeleteNodeConversion;
import nl.cad.json.transform.transforms.convert.OverwriteValueConversion;
import nl.cad.json.transform.transforms.convert.RelativeMovePropertyConversion;
import nl.cad.json.transform.transforms.convert.RenamePropertyConversion;

/**
 * construct property mappings on a single object.
 */
public class PropertyMappingBuilder {

    public static PropertyMappingBuilder map() {
        return new PropertyMappingBuilder();
    }

    private List<TransformSelect> transformSelects;

    public PropertyMappingBuilder() {
        transformSelects = new ArrayList<TransformSelect>();
    }

    public MappingTransform build() {
        return new MappingTransform(new ArrayList<TransformSelect>(transformSelects));
    }

    public PropertyMappingBuilder mapping(ValuePathTransform transform, Select select) {
        transformSelects.add(new TransformSelect(transform, select));
        return this;
    }

    public PropertyMappingBuilder postMapping(ValuePathTransform transform, Select select) {
        transformSelects.add(new TransformSelect(transform, select, true));
        return this;
    }

    /**
     * applies a transform against an object of a specific type.
     * @param transform the transform.
     * @param typeProperty the property holding the type.
     * @param value the type value.
     * @return the mapper.
     */
    public PropertyMappingBuilder objectMapping(ValuePathTransform transform, String property, String value) {
        return mapping(transform, SelectBuilder.select().objectPropertyValue(property, value).build());
    }

    public PropertyMappingBuilder objectMapping(MappingTransform mapping, String property, String value) {
        return mapping(mapping, SelectBuilder.select().objectPropertyValue(property, value).build());
    }

    /**
     * renames all properties with the given name.
     * @param fromName the name to rename from.
     * @param toName the name to rename to.
     * @return the mapper.
     */
    public PropertyMappingBuilder rename(final String fromName, final String toName) {
        return mapping(new RenamePropertyConversion(toName), SelectBuilder.select().property(fromName).build());
    }

    public PropertyMappingBuilder move(final String property, RelativePath path) {
        return mapping(new RelativeMovePropertyConversion(path), SelectBuilder.select().property(property).build());
    }

    public PropertyMappingBuilder move(final String property, Path path) {
        return mapping(new AbsoluteMovePropertyConversion(path), SelectBuilder.select().property(property).build());
    }

    public PropertyMappingBuilder overwrite(final String property, final String targetValue) {
        return mapping(new OverwriteValueConversion(targetValue), SelectBuilder.select().property(property).build());
    }

    public PropertyMappingBuilder overwrite(final String property, String value, final String targetValue) {
        return mapping(new OverwriteValueConversion(targetValue), SelectBuilder.select().propertyValue(property, value).build());
    }

    public PropertyMappingBuilder recurse(final String property, final List<TransformSelect> mappers) {
        return mapping(new MappingTransform(mappers), SelectBuilder.select().property(property).build());
    }

    public PropertyMappingBuilder recurse(final String property, MappingTransform mappers) {
        return mapping(mappers, SelectBuilder.select().property(property).build());
    }

    public PropertyMappingBuilder delete(final String property) {
        return mapping(new DeleteNodeConversion(), SelectBuilder.select().property(property).build());
    }

    public PropertyMappingBuilder reformat(final String property, ValuePathTransform conversion) {
        return mapping(conversion, SelectBuilder.select().property(property).build());
    }

    public PropertyMappingBuilder add(final String property, final Object value) {
        return postMapping(new AddPropertyConversion(property, value), SelectBuilder.selectRoot());
    }


}
