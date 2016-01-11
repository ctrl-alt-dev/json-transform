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

import java.util.List;

import nl.cad.json.transform.mapping.MoveTransformSelect;
import nl.cad.json.transform.mapping.builder.sub.MultiBuilder;
import nl.cad.json.transform.mapping.builder.sub.MultiMoveBuilder;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.select.SelectBuilder;
import nl.cad.json.transform.template.Template;
import nl.cad.json.transform.transforms.IdentityTransform;
import nl.cad.json.transform.transforms.JavaTransform;
import nl.cad.json.transform.transforms.MappingTransform;
import nl.cad.json.transform.transforms.NopTransform;
import nl.cad.json.transform.transforms.TemplateAdapter;
import nl.cad.json.transform.transforms.Transform;
import nl.cad.json.transform.transforms.ValuePathTransform;
import nl.cad.json.transform.transforms.ValuePathTransformAdapter;
import nl.cad.json.transform.transforms.value.IsSelectionPresentTransform;

/**
 * constructs mappings consisting of a move, transform and select.
 */
public class MappingBuilder {

    /**
     * starts building a mapping that takes multiple selects and multiple output paths.
     * @return the multibuilder.
     */
    public static MultiMoveBuilder manyToMany() {
        return new MultiBuilder();
    }

    /**
     * takes multiple selections from the input, applies the transform and moves the result
     * to the specified paths in the output. It is probably easier to use manyToMany() than
     * this using this method directly.
     * @param moves the paths to move the result to.
     * @param transform the transform to apply.
     * @param selects the selects to use to find nodes.
     * @return the transform.
     */
    public static Transform transform(List<Path> moves, Transform transform, List<Select> selects) {
        return new MoveTransformSelect(moves, transform, selects);
    }

    /**
     * takes a selection from the input, applies the transform and moves it to the
     * specified location in the output.
     * @param move the location to move to.
     * @param transform the transform.
     * @param select the selection.
     * @return the builder.
     */
    public static Transform transform(Path move, Transform transform, Select select) {
        return new MoveTransformSelect(move, transform, select);
    }

    /**
     * copies the input to the output.
     * @return the builder.
     */
    public static Transform copy() {
        return transform(Path.root(), new IdentityTransform(), SelectBuilder.selectRoot());
    }

    /**
     * copies and moves the input to the specified path in the output document.
     * @param path the path.
     * @return the builder.
     */
    public static Transform move(Path path) {
        return transform(path, new NopTransform(), SelectBuilder.selectRoot());
    }

    /**
     * copies and moves the selection to the specified path in the output document.
     * @param path the path.
     * @param select the selection.
     * @return the builder.
     */
    public static Transform move(Path path, Select select) {
        return transform(path, new NopTransform(), select);
    }

    /**
     * transforms the whole input to the output document.
     * @param transform the input
     * @return the builder.
     */
    public static Transform transform(Transform transform) {
        return transform(Path.root(), transform, SelectBuilder.selectRoot());
    }

    /**
     * transforms the selection from the input to the output.
     * @param transform the transform.
     * @param select the selection.
     * @return the builder.
     */
    public static Transform transform(Transform transform, Select select) {
        return transform(Path.root(), transform, select);
    }

    /**
     * transforms and moves the input document to the output at the given path.
     * @param path the path.
     * @param transform the transform.
     * @return the builder.
     */
    public static Transform transform(Path path, Transform transform) {
        return transform(path, transform, SelectBuilder.selectRoot());
    }
    
    /**
     * applies a template to the input document.
     * @param template the template.
     * @return the builder.
     */
    public static Transform template(Template template) {
        return transform(new TemplateAdapter(template));
    }

    /**
     * applies a template to a selection of the input document.
     * @param template the template.
     * @param select the selection.
     * @return the builder.
     */
    public static Transform template(Template template, Select select) {
        return transform(new TemplateAdapter(template), select);
    }

    /**
     * applies a template to the input document and moves the result to the given path.
     * @param path the path.
     * @param template the template.
     * @return the builder.
     */
    public static Transform template(Path path, Template template) {
        return transform(path, new TemplateAdapter(template));
    }

    /**
     * applies a template to a selection of the input document and moves the result to the given path.
     * @param path the path.
     * @param template the template.
     * @param select the selection.
     * @return the builder.
     */
    public static Transform template(Path path, Template template, Select select) {
        return transform(path, new TemplateAdapter(template), select);
    }

    /**
     * applies a java mapping to the input document and moves the result to the root.
     * @param <A> the type to return.
     * @param type the Java input type.
     * @param function the function to execute.
     * @return the builder.
     */

    public static <A> Transform javaTransform(Class<A> type, JavaTransform.JavaInvoke<A> function) {
        return transform(new JavaTransform<A>(type, function));
    }

    /**
     * applies a java mapping to the input document and moves the result to the given path.
     * @param <A> the type to return.
     * @param path the path.
     * @param type the Java input type.
     * @param function the function to execute.
     * @return the builder.
     */
    public static <A> Transform javaTransform(Path path, Class<A> type, JavaTransform.JavaInvoke<A> function) {
        return transform(path, new JavaTransform<A>(type, function));
    }

    /**
     * applies a java mapping to the selection of the input document and moves the result to the root.
     * @param <A> the type to return.
     * @param type the Java input type.
     * @param function the function to execute.
     * @param select the selection.
     * @return the builder.
     */
    public static <A> Transform javaTransform(Class<A> type, JavaTransform.JavaInvoke<A> function, Select select) {
        return transform(new JavaTransform<A>(type, function), select);
    }

    /**
     * applies a java mapping to the selection of the input document and moves the result to the given path.
     * @param <A> the type to return.
     * @param path the path.
     * @param type the Java input type.
     * @param function the function to execute.
     * @param select the selection.
     * @return the builder.
     */
    public static <A> Transform javaTransform(Path path, Class<A> type, JavaTransform.JavaInvoke<A> function, Select select) {
        return transform(path, new JavaTransform<A>(type, function), select);
    }

    /**
     * assigns value true to the path if the selection has at least one result.
     * @param path the path to place the boolean value.
     * @param select the select.
     * @return the builder.
     */
    public static Transform exists(Path path, Select select) {
        return transform(path, new IsSelectionPresentTransform(), select);
    }

    /**
     * applies a mapping transform to the selected nodes and moves the result to the given path.
     * @param path the path.
     * @param mapping the mapping.
     * @param select the selection.
     * @return the builder.
     */
    public static Transform map(Path path, MappingTransform mapping, Select select) {
        return transform(path, mapping, select);
    }

    /**
     * applies a value path transform to the selected nodes and moves the result to the given path.
     * @param move the path to move to.
     * @param transform the value path transform.
     * @param select the selection.
     * @return the builder.
     */
    public static Transform map(Path move, ValuePathTransform transform, Select select) {
        return transform(move, new ValuePathTransformAdapter(transform), select);
    }

    /**
     * applies a value path transform to the selected nodes and moves the result to root.
     * @param transform the value path transform.
     * @param select the selection.
     * @return the builder.
     */
    public static Transform map(ValuePathTransform transform, Select select) {
        return transform(Path.root(), new ValuePathTransformAdapter(transform), select);
    }

}
