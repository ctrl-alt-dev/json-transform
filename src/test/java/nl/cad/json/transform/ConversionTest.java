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
package nl.cad.json.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.transforms.convert.RenamePropertyConversion;
import nl.cad.json.transform.transforms.convert.TimestampToFormattedLocalDateTimeConversion;
import nl.cad.json.transform.transforms.convert.ToStringValueConversion;

import org.junit.Test;

public class ConversionTest {

    private Path path = Path.root().enter("path");

    @Test
    public void shouldToString() {
        Map<String, Object> target = new ToStringValueConversion().apply(path, Long.valueOf(42));
        assertEquals("42", path.get(target));
    }

    @Test
    public void shouldToFormatTimestamp() {
        Map<String, Object> target = new TimestampToFormattedLocalDateTimeConversion("yyyy-MM-dd").apply(path, Long.valueOf(0L));
        assertNotNull(path.get(target));
    }

    @Test
    public void shouldRenamePropertyTransform() {
        Map<String, Object> target = new RenamePropertyConversion("name").apply(path, Long.valueOf(0L));
        assertNotNull(path.parent().enter("name").get(target));
    }

}
