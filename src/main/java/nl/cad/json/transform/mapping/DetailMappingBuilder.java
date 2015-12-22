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
package nl.cad.json.transform.mapping;

import java.util.ArrayList;
import java.util.List;

import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.select.SelectBuilder;
import nl.cad.json.transform.transforms.MappingTransform;
import nl.cad.json.transform.transforms.ValuePathTransform;
import nl.cad.json.transform.transforms.convert.DeleteNodeConversion;
import nl.cad.json.transform.transforms.convert.OverwriteValueConversion;
import nl.cad.json.transform.transforms.convert.RenamePropertyConversion;

public class DetailMappingBuilder {

    public static DetailMappingBuilder map() {
        return new DetailMappingBuilder();
    }

    private List<TransformSelect> transformSelects;

    public DetailMappingBuilder() {
        transformSelects = new ArrayList<TransformSelect>();
    }

    public List<TransformSelect> build() {
        return new ArrayList<TransformSelect>(transformSelects);
    }

    public DetailMappingBuilder mapping(ValuePathTransform transform, Select select) {
        transformSelects.add(new TransformSelect(transform, select));
        return this;
    }

    /**
     * applies a transform against an object of a specific type.
     * @param transform the transform.
     * @param typeProperty the property holding the type.
     * @param value the type value.
     * @return the mapper.
     */
    public DetailMappingBuilder objectMapping(ValuePathTransform transform, String property, String value) {
        return mapping(transform, SelectBuilder.select().objectPropertyValue(property, value).build());
    }

    public DetailMappingBuilder objectMapping(List<TransformSelect> transformSelects, String property, String value) {
        return mapping(new MappingTransform(transformSelects), SelectBuilder.select().objectPropertyValue(property, value).build());
    }

    /**
     * renames all properties with the given name.
     * @param toName the name to rename to.
     * @param fromName the name to rename from.
     * @return the mapper.
     */
    public DetailMappingBuilder rename(final String toName,final String fromName) {
        return mapping(new RenamePropertyConversion(toName), SelectBuilder.select().property(fromName).build());
    }

    public DetailMappingBuilder overwrite(final String property, final String targetValue) {
        return mapping(new OverwriteValueConversion(targetValue), SelectBuilder.select().property(property).build());
    }

    public DetailMappingBuilder overwrite(final String property, String value, final String targetValue) {
        return mapping(new OverwriteValueConversion(targetValue), SelectBuilder.select().propertyValue(property, value).build());
    }

    public DetailMappingBuilder recurse(final String property, final List<TransformSelect> mappers) {
        return mapping(new MappingTransform(mappers), SelectBuilder.select().property(property).build());
    }

    public DetailMappingBuilder delete(String property) {
        return mapping(new DeleteNodeConversion(), SelectBuilder.select().property(property).build());
    }

}
