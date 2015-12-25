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

import nl.cad.json.transform.mapping.map.JoinMapper;
import nl.cad.json.transform.mapping.map.ParallelMapper;
import nl.cad.json.transform.mapping.map.SequenceMapper;
import nl.cad.json.transform.mapping.source.DocumentSource;
import nl.cad.json.transform.mapping.source.EchoSource;
import nl.cad.json.transform.mapping.source.NamedSource;
import nl.cad.json.transform.mapping.source.ValueSource;
import nl.cad.json.transform.merge.MergeFactory;
import nl.cad.json.transform.merge.MergeStrategy;
import nl.cad.json.transform.transforms.Transform;

/**
 * Builder to link mappings together.
 * <ul>
 * <li>Sequence: the output of one transform is the input for the other.</li>
 * <li>Parallel: the same input is used for all the transform and the results will be merged into one document.</li>
 * <li>Join: multiple document sources will be merged into one document.</li>
 * <li>Split: a single document source will be split using named selects into multiple.</li>
 * </ul>
 */
public class CompositeMappingBuilder {

    /**
     * construct a mapper that will apply multiple transform to same input,
     * merging the results using the provided merge strategy.
     * @param merge the merge strategy to use.
     * @return the builder.
     */
    public static CompositeMappingBuilder parallel(MergeStrategy merge, Transform... transforms) {
        return new CompositeMappingBuilder(merge, transforms);
    }

    /**
     * constructs a mapper that will apply transform sequentially to each other.
     * @return the builder.
     */
    public static CompositeMappingBuilder sequence(Transform... transforms) {
        return new CompositeMappingBuilder(null, transforms);
    }

    /**
     * joins multiple document sources into one.
     * @param sources the sources.
     * @return the joined document source.
     */
    public static DocumentSource join(DocumentSource... sources) {
        return new JoinMapper(MergeFactory.join(), sources);
    }

    /**
     * splits a single document source into multiple named ones.
     * @param source the source to split.
     * @return the builder to add named selects.
     */
    public static SplitSourceBuilder split(DocumentSource source) {
        return new SplitSourceBuilder(source);
    }

    private final MergeStrategy merge;
    private final List<Transform> ops;

    protected CompositeMappingBuilder(MergeStrategy merge, Transform[] transforms) {
        this.merge = merge;
        this.ops = new ArrayList<Transform>(Arrays.asList(transforms));
    }

    /**
     * adds additional transforms.
     * @param transforms the transforms.
     * @return the builder.
     */
    public CompositeMappingBuilder add(Transform... transforms) {
        this.ops.addAll(Arrays.asList(transforms));
        return this;
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
