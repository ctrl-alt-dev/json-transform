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

import java.util.Map;

import nl.cad.json.transform.java.DocumentToJavaMapper;
import nl.cad.json.transform.java.JavaToDocumentMapper;
import nl.cad.json.transform.java.pojo.PojoFromDocumentMapper;
import nl.cad.json.transform.java.pojo.PojoToDocumentMapper;
import nl.cad.json.transform.merge.JoinMergeStrategy;
import nl.cad.json.transform.path.Path;

/**
 * Performs a transform in Java using a function and Java types.
 * 
 * The selection is deserialized to an object of the given Java type,
 * which is then passed to the JavaInvoke interface and the result is
 * serialized back to a document.
 * 
 * @param <A> the java type that should be applied to the selection(s).
 */
public class JavaTransform<A> implements Transform {

    @FunctionalInterface
    public interface JavaInvoke<A> {
        Object transform(A a);
    }
    
    private Class<A> argType;
    private DocumentToJavaMapper deserializer;
    private JavaToDocumentMapper serializer;
    private JavaInvoke<A> function;
    private JoinMergeStrategy join;

    public JavaTransform(Class<A> argType, JavaInvoke<A> function) {
        this(argType, new PojoFromDocumentMapper(), new PojoToDocumentMapper(), function);
    }

    public JavaTransform(Class<A> argType, DocumentToJavaMapper deserializer, JavaToDocumentMapper serializer, JavaInvoke<A> function) {
        this.argType = argType;
        this.deserializer = deserializer;
        this.serializer = serializer;
        this.function = function;
        this.join = new JoinMergeStrategy(Path.root());
    }

    @Override
    public void apply(Path path, Object source, Map<String, Object> target) {
        A javaSource = deserializer.toJava(argType, source);
        Object javaResult = function.transform(javaSource);
        Object result = serializer.toDocument(javaResult);
        join.merge(result, target);
    }
}
