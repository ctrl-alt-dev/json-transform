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

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import nl.cad.json.transform.path.ValuePath;
import nl.cad.json.transform.transforms.ValuePathTransform;

public class ReformatDateTimeConversion implements ValuePathTransform {

    private DateTimeFormatter from;
    private DateTimeFormatter to;

    public ReformatDateTimeConversion(String fromFormat, String toFormat) {
        this(DateTimeFormatter.ofPattern(fromFormat), DateTimeFormatter.ofPattern(toFormat));
    }

    public ReformatDateTimeConversion(DateTimeFormatter from, DateTimeFormatter to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void apply(ValuePath source, ValuePath target) {
        TemporalAccessor parse = from.parse(String.valueOf(source.get()));
        target.set(to.format(parse));
    }
}
