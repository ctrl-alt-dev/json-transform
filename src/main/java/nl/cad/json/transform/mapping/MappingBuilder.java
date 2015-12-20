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

import nl.cad.json.transform.mapping.map.JoinMapper;
import nl.cad.json.transform.mapping.map.ParallelMapper;
import nl.cad.json.transform.mapping.map.SequenceMapper;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.EchoSource;
import nl.cad.json.transform.mapping.source.MultiSource;
import nl.cad.json.transform.mapping.source.NamedSource;
import nl.cad.json.transform.mapping.source.ValueSource;
import nl.cad.json.transform.merge.MergeFactory;
import nl.cad.json.transform.merge.MergeStrategy;
import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.select.Select;
import nl.cad.json.transform.select.SelectBuilder;
import nl.cad.json.transform.template.Template;
import nl.cad.json.transform.transforms.IdentityTransform;
import nl.cad.json.transform.transforms.JavaTransform;
import nl.cad.json.transform.transforms.TemplateAdapter;
import nl.cad.json.transform.transforms.Transform;
import nl.cad.json.transform.transforms.convert.IsSelectionPresentTransform;

/**
 * Builds mappers.
 * 
 * <p>
 * A mapper is built, starting from the output, applying move, transforms and selects back to the input(s).
 * </p>
 * 
 * <h3>Basic operations</h3>
 * <p>
 * There are three basic operations, which can be combined in each step:
 * </p>
 * <ul>
 * <li>Move : moves a result to some path in the output document (default: root).</li>
 * <li>Transform : transforms an input selection to a different result (default: identity).</li>
 * <li>Select: Takes a selection from the input. (default: the root of the input)</li>
 * </ul>
 * 
 * <pre>
 *   output &lt;-- ( Move &lt;- Transform &lt;- Select ) &lt;-- input
 * </pre>
 * 
 * <h3>Sequential and Parallel</h3>
 * 
 * <p>
 * This builder constructs mappings in two basic configurations:
 * </p>
 * <ul>
 * <li>sequential mappings, applying Move-Transform-Selects sequentially in reverse order, by taking the output of one as the input of the next.</li>
 * <li>parallel mappings, applying the same input to each of the Move-Transform-Selects and merging the results using a {@link MergeStrategy}.</li>
 * </ul>
 * 
 * <pre>
 * output &lt;-- ( Move &lt;- Transform &lt;- Select ) &lt;- ( Move &lt;- Transform &lt;- Select ) &lt;-- input
 * </pre>
 * 
 * <pre>
 * output &lt;-- Merge &lt;- ( Move &lt;- Transform &lt;- Select ) &lt;-- input
 *                   \-( Move &lt;- Transform &lt;- Select ) -/
 * </pre>
 * 
 * <h3>Input and Output</h3>
 * <p>
 * Input and output are represented as {@link DocumentSource}s, which functions as a pull based mechanism, you ask the document source constructed from this
 * builder for its document and in turn it will ask any linked document sources for theirs and apply its transforms.
 * </p>
 * 
 * <p>
 * In order to make these mappings thread safe and reusable you can provide the document source with another document source as its input. That document source
 * will be used at the end (or actually the beginning) of the chain.
 * </p>
 * 
 * <p>
 * Sometimes you want to have multiple inputs. This can be achieved by using named sources in the builder and the {@link MultiSource} as input value.
 * </p>
 */
public class MappingBuilder {

    /**
     * construct a mapper that will apply multiple move-transform-selects to same input,
     * merging the results using the provided merge strategy.
     * @param merge the merge strategy to use.
     * @return the builder.
     */
    public static MappingBuilder par(MergeStrategy merge) {
        return new MappingBuilder(merge);
    }

    /**
     * constructs a mapper that will apply move-transform-selects sequentially to each other.
     * @return the builder.
     */
    public static MappingBuilder seq() {
        return new MappingBuilder(null);
    }

    /**
     * joins multiple document sources into one.
     * @param sources the sources.
     * @return the joined document source.
     */
    public static DocumentSource join(DocumentSource... sources) {
        return new JoinMapper(MergeFactory.join(), sources);
    }
    
    private MergeStrategy merge;
    private List<MoveTransformSelect> ops;

    protected MappingBuilder(MergeStrategy merge) {
        this.merge = merge;
        this.ops = new ArrayList<MoveTransformSelect>();
    }

    /**
     * takes a selection from the input, applies the transform and moves it to the
     * specified location in the output.
     * @param move the location to move to.
     * @param transform the transform.
     * @param select the selection.
     * @return the builder.
     */
    public MappingBuilder transform(Path move, Transform transform, Select select) {
        ops.add(new MoveTransformSelect(move, transform, select));
        return this;
    }

    /**
     * copies the input to the output.
     * @return the builder.
     */
    public MappingBuilder copy() {
        return this.move(Path.root());
    }

    /**
     * copies and moves the input to the specified path in the output document.
     * @param path the path.
     * @return the builder.
     */
    public MappingBuilder move(Path path) {
        return this.transform(path, new IdentityTransform(), SelectBuilder.selectRoot());
    }

    /**
     * copies and moves the selection to the specified path in the output document.
     * @param path the path.
     * @param select the selection.
     * @return the builder.
     */
    public MappingBuilder move(Path path, Select select) {
        return this.transform(path, new IdentityTransform(), select);
    }

    /**
     * transforms the whole input to the output document.
     * @param transform the input
     * @return the builder.
     */
    public MappingBuilder transform(Transform transform) {
        return this.transform(Path.root(), transform, SelectBuilder.selectRoot());
    }

    /**
     * transforms the selection from the input to the output.
     * @param transform the transform.
     * @param select the selection.
     * @return the builder.
     */
    public MappingBuilder transform(Transform transform, Select select) {
        return this.transform(Path.root(), transform, select);
    }

    /**
     * transforms and moves the input document to the output at the given path.
     * @param path the path.
     * @param transform the transform.
     * @return the builder.
     */
    public MappingBuilder transform(Path path, Transform transform) {
        return this.transform(path, transform, SelectBuilder.selectRoot());
    }
    
    /**
     * applies a template to the input document.
     * @param template the template.
     * @return the builder.
     */
    public MappingBuilder template(Template template) {
        return this.transform(new TemplateAdapter(template));
    }

    /**
     * applies a template to a selection of the input document.
     * @param template the template.
     * @param select the selection.
     * @return the builder.
     */
    public MappingBuilder template(Template template, Select select) {
        return this.transform(new TemplateAdapter(template), select);
    }

    /**
     * applies a template to the input document and moves the result to the given path.
     * @param path the path.
     * @param template the template.
     * @return the builder.
     */
    public MappingBuilder template(Path path, Template template) {
        return this.transform(path, new TemplateAdapter(template));
    }

    /**
     * applies a template to a selection of the input document and moves the result to the given path.
     * @param path the path.
     * @param template the template.
     * @param select the selection.
     * @return the builder.
     */
    public MappingBuilder template(Path path, Template template, Select select) {
        return this.transform(path, new TemplateAdapter(template), select);
    }

    /**
     * applies a java mapping to the input document and moves the result to the root.
     * @param type the Java input type.
     * @param function the function to execute.
     * @return the builder.
     */

    public <A> MappingBuilder javaTransform(Class<A> type, JavaTransform.JavaInvoke<A> function) {
        return this.transform(new JavaTransform<A>(type, function));
    }

    /**
     * applies a java mapping to the input document and moves the result to the given path.
     * @param path the path.
     * @param type the Java input type.
     * @param function the function to execute.
     * @return the builder.
     */
    public <A> MappingBuilder javaTransform(Path path, Class<A> type, JavaTransform.JavaInvoke<A> function) {
        return this.transform(path, new JavaTransform<A>(type, function));
    }

    /**
     * applies a java mapping to the selection of the input document and moves the result to the root.
     * @param type the Java input type.
     * @param function the function to execute.
     * @param select the selection.
     * @return the builder.
     */
    public <A> MappingBuilder javaTransform(Class<A> type, JavaTransform.JavaInvoke<A> function, Select select) {
        return this.transform(new JavaTransform<A>(type, function), select);
    }

    /**
     * applies a java mapping to the selection of the input document and moves the result to the given path.
     * @param path the path.
     * @param type the Java input type.
     * @param function the function to execute.
     * @param select the selection.
     * @return the builder.
     */
    public <A> MappingBuilder javaTransform(Path path, Class<A> type, JavaTransform.JavaInvoke<A> function, Select select) {
        return this.transform(path, new JavaTransform<A>(type, function), select);
    }

    /**
     * assigns value true to the path if the selection has at least one result.
     * @param path the path to place the boolean value.
     * @param select the select.
     * @return the builder.
     */
    public MappingBuilder exists(Path path, Select select) {
        return this.transform(path, new IsSelectionPresentTransform(), select);
    }

    /**
     * Ends this builder by giving it its document source.
     * @param src the document source for this mapper.
     * @return the document source of the constructed mapper.
     */
    public DocumentSource link(DocumentSource src) {
        if (merge == null) {
            return new SequenceMapper(ops, src);
        } else {
            return new ParallelMapper(merge, ops, src);
        }
    }

    /**
     * Ends this builder by giving it a named document source.
     * @param name the name.
     * @return the document source of the constructed mapper.
     */
    public DocumentSource namedSource(String name) {
        return link(new NamedSource(name));
    }

    /**
     * Ends this builder. The constructed mapper will use the provided
     * document source passed as an argument to its getDocument method.
     * @return the document source of the constructed mapper.
     */
    public DocumentSource build() {
        return link(new EchoSource());
    }

    /**
     * Ends this builder. The constructed mapper uses the provided
     * value as its (fixed) input.
     * @param value the value.
     * @return the document source of the constructed mapper.
     */
    public DocumentSource fixedSource(Object value) {
        return link(new ValueSource(value));
    }

}
