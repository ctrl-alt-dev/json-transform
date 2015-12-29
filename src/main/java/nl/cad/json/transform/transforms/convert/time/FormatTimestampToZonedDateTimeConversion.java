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
package nl.cad.json.transform.transforms.convert.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.transforms.ValuePathTransform;

public class FormatTimestampToZonedDateTimeConversion implements ValuePathTransform {

    private DateTimeFormatter formatter;

    public FormatTimestampToZonedDateTimeConversion() {
        this(DateTimeFormatter.ISO_DATE_TIME);
    }

    public FormatTimestampToZonedDateTimeConversion(String format) {
        this(DateTimeFormatter.ofPattern(format));
    }

    public FormatTimestampToZonedDateTimeConversion(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void apply(ValuePath source, ValuePath target) {
        long timestamp = Long.valueOf(String.valueOf(source.get()));
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault());
        target.set(formatter.format(zonedDateTime));
    }

}
