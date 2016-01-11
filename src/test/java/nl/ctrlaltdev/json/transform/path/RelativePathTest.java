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
package nl.ctrlaltdev.json.transform.path;

import static org.junit.Assert.assertEquals;
import nl.ctrlaltdev.json.transform.path.Path;
import nl.ctrlaltdev.json.transform.path.relative.RelativePath;
import nl.ctrlaltdev.json.transform.path.relative.RelativePathBuilder;
import nl.ctrlaltdev.json.transform.path.relative.ParentMove.MoveBeyondRootException;

import org.junit.Test;

public class RelativePathTest {

    @Test
    public void shouldRelativeProperty() {
        RelativePath relative = RelativePathBuilder.relativePath().parent().parent().property("over").property("the").property("rainbow").build();
        Path absolute = Path.fromString("some.property.some.where");
        //
        assertEquals("some.property.over.the.rainbow", relative.apply(absolute).toString());
    }

    @Test
    public void shouldRelativeIndex() {
        RelativePath relative = RelativePathBuilder.relativePath().parent().parent().index(42).property("there").build();
        Path absolute = Path.fromString("some[1].here");
        //
        assertEquals("some[42].there", relative.apply(absolute).toString());
    }

    @Test
    public void shouldNop() {
        RelativePath relative = RelativePathBuilder.relativePath().build();
        Path absolute = Path.fromString("some.property.some.where");
        //
        assertEquals("some.property.some.where", relative.apply(absolute).toString());
    }

    @Test
    public void shouldMoveToRoot() {
        RelativePath relative = RelativePathBuilder.relativePath().parent().parent().parent().parent().build();
        Path absolute = Path.fromString("some.property.some.where");
        //
        assertEquals("", relative.apply(absolute).toString());
    }

    @Test(expected = MoveBeyondRootException.class)
    public void shouldFailBeyondRoot() {
        RelativePath relative = RelativePathBuilder.relativePath().parent().parent().parent().parent().parent().build();
        Path absolute = Path.fromString("some.property.some.where");
        //
        assertEquals("some.property.some.where", relative.apply(absolute).toString());
    }

}
