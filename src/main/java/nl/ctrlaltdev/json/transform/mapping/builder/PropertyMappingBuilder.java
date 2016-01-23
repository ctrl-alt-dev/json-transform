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
package nl.ctrlaltdev.json.transform.mapping.builder;

import java.util.ArrayList;
import java.util.List;

import nl.ctrlaltdev.json.transform.mapping.TransformSelect;
import nl.ctrlaltdev.json.transform.path.Path;
import nl.ctrlaltdev.json.transform.path.relative.RelativePath;
import nl.ctrlaltdev.json.transform.path.relative.RelativePathBuilder;
import nl.ctrlaltdev.json.transform.select.Select;
import nl.ctrlaltdev.json.transform.select.SelectBuilder;
import nl.ctrlaltdev.json.transform.transforms.MappingTransform;
import nl.ctrlaltdev.json.transform.transforms.ValuePathTransform;
import nl.ctrlaltdev.json.transform.transforms.structural.AbsoluteMovePropertyConversion;
import nl.ctrlaltdev.json.transform.transforms.structural.AddPropertyConversion;
import nl.ctrlaltdev.json.transform.transforms.structural.DeleteNodeConversion;
import nl.ctrlaltdev.json.transform.transforms.structural.FlattenPropertyConversion;
import nl.ctrlaltdev.json.transform.transforms.structural.ListifyConversion;
import nl.ctrlaltdev.json.transform.transforms.structural.MapifyConversion;
import nl.ctrlaltdev.json.transform.transforms.structural.RaiseConversion;
import nl.ctrlaltdev.json.transform.transforms.structural.RelativeMovePropertyConversion;
import nl.ctrlaltdev.json.transform.transforms.structural.SkipConversion;
import nl.ctrlaltdev.json.transform.transforms.value.ComputeAndAddValueTransform;
import nl.ctrlaltdev.json.transform.transforms.value.ComputeValueTransform;
import nl.ctrlaltdev.json.transform.transforms.value.ComputeValueTransform.Computation;
import nl.ctrlaltdev.json.transform.transforms.value.OverwriteValueConversion;

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

    /**
     * adds a mapping.
     * @param transform the transform.
     * @param select the selection.
     * @return the builder.
     */
    public PropertyMappingBuilder mapping(ValuePathTransform transform, Select select) {
        transformSelects.add(new TransformSelect(transform, select));
        return this;
    }

    /**
     * adds a mapping that will be executed after all the other mappings.
     * @param transform the transform.
     * @param select the select.
     * @return the builder.
     */
    public PropertyMappingBuilder postMapping(ValuePathTransform transform, Select select) {
        transformSelects.add(new TransformSelect(transform, select, true));
        return this;
    }

    /**
     * applies a transform against an object of a specific type.
     * @param transform the transform.
     * @param property the property holding the type.
     * @param value the type value.
     * @return the builder.
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
     * @return the builder.
     */
    public PropertyMappingBuilder rename(final String fromName, final String toName) {
        return move(fromName, RelativePathBuilder.relativePath().parent().property(toName).build());
    }

    /**
     * moves the property relative from its current position in the document.
     * @param property the property to move.
     * @param path the relative path.
     * @return the builder.
     */
    public PropertyMappingBuilder move(final String property, RelativePath path) {
        return mapping(new RelativeMovePropertyConversion(path), selectProperty(property));
    }

    /**
     * moves the property to an absolute position in the document.
     * @param property the property to move.
     * @param path the absolute path.
     * @return the builder.
     */
    public PropertyMappingBuilder move(final String property, Path path) {
        return mapping(new AbsoluteMovePropertyConversion(path), selectProperty(property));
    }

    /**
     * overwrites the property with a fixed value.
     * @param property the property.
     * @param targetValue the value.
     * @return the builder.
     */
    public PropertyMappingBuilder overwrite(final String property, final String targetValue) {
        return mapping(new OverwriteValueConversion(targetValue), selectProperty(property));
    }

    /**
     * overwrites the property with the given value with another value.
     * @param property the property.
     * @param value the property value to overwrite.
     * @param targetValue the new value.
     * @return the builder.
     */
    public PropertyMappingBuilder overwrite(final String property, String value, final String targetValue) {
        return mapping(new OverwriteValueConversion(targetValue), SelectBuilder.select().propertyValue(property, value).build());
    }

    /**
     * applies mapping to the property.
     * Use this form when you want to re-use the same mapping as you're currently building by
     * giving it an empty List and populating the list after construction of this mapping.
     * @param property the property.
     * @param mappers the mappers to apply
     * @return the builder.
     */
    public PropertyMappingBuilder recurse(final String property, final List<TransformSelect> mappers) {
        return mapping(new MappingTransform(mappers), selectProperty(property));
    }

    /**
     * applies mapping to the property.
     * @param property the property.
     * @param mappers the mappers to apply
     * @return the builder.
     */
    public PropertyMappingBuilder recurse(final String property, MappingTransform mappers) {
        return mapping(mappers, selectProperty(property));
    }

    /**
     * skips the specified properties, not copying them to the output and also not visiting its child properties.
     * @param properties the properties.
     * @return the mapper.
     */
    public PropertyMappingBuilder skip(final String... properties) {
        for (String pr : properties) {
            mapping(new SkipConversion(), selectProperty(pr));
        }
        return this;
    }

    /**
     * flattens the specified property into the parent object.
     * All named child properties that hold a value will be added to the parent object.
     * @param property the property to flatten.
     * @param excludes the sub-properties to exclude.
     * @return the builder.
     */
    public PropertyMappingBuilder flatten(final String property, String... excludes) {
        return mapping(new FlattenPropertyConversion(excludes), selectProperty(property));
    }

    /**
     * raise moves the specified property's values to the leaf objects of that graph.
     * @param property the property to raise.
     * @param excludes the sub-properties to exclude.
     * @return the builder.
     */
    public PropertyMappingBuilder raise(final String property, String... excludes) {
        return mapping(new RaiseConversion(excludes), selectProperty(property));
    }

    /**
     * converts an array into a map by using the keyProperty as the key value.
     * @param property the property to apply this transformation on (must be a list of objects).
     * @param keyProperty the key property to use for key values.
     * @return the builder.
     */
    public PropertyMappingBuilder mapify(final String property, final String keyProperty) {
        return mapping(new MapifyConversion(keyProperty), selectProperty(property));
    }

    /**
     * converts an map into an array by setting the keyProperty with the key value.
     * @param property the property to apply this transformation on (must be an object).
     * @param keyProperty the key property to set the key value on.
     * @return the builder.
     */
    public PropertyMappingBuilder listify(final String property, final String keyProperty) {
        return mapping(new ListifyConversion(keyProperty), selectProperty(property));
    }

    /**
     * deletes a node from the target document.
     * @param property the property.
     * @return the builder.
     */
    public PropertyMappingBuilder delete(final String property) {
        return mapping(new DeleteNodeConversion(), selectProperty(property));
    }

    public PropertyMappingBuilder reformat(final String property, ValuePathTransform conversion) {
        return mapping(conversion, selectProperty(property));
    }

    /**
     * computes the new value of the property.
     * @param property the property.
     * @param computation the computation.
     * @return the builder.
     */
    public PropertyMappingBuilder compute(final String property, Computation computation) {
        return mapping(new ComputeValueTransform(computation), selectProperty(property));
    }

    /**
     * computes a new value based on the selected property and adds a new property holding that value.
     * @param selectProperty the property to select.
     * @param addProperty the property to add.
     * @param computation the computation.
     * @return the builder.
     */
    public PropertyMappingBuilder computeAndAdd(final String selectProperty, final String addProperty, Computation computation) {
        return mapping(new ComputeAndAddValueTransform(addProperty, computation), selectProperty(selectProperty));
    }

    /**
     * adds a single property with the given value.
     * @param property the property to add.
     * @param value its value.
     * @return the builder.
     */
    public PropertyMappingBuilder add(final String property, final Object value) {
        return postMapping(new AddPropertyConversion(property, value), SelectBuilder.selectRoot());
    }

    private Select selectProperty(final String property) {
        return SelectBuilder.select().property(property).build();
    }

}
