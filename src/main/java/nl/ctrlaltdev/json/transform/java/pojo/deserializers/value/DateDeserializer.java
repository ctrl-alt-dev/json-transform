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
package nl.ctrlaltdev.json.transform.java.pojo.deserializers.value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class DateDeserializer implements ValueDeserializer {

    private List<Class<?>> types;
    private String property;
    private Class<?> ownerType;
    private DateTimeFormatter format;

    public DateDeserializer(String format, String property) {
        this(format);
        this.property = property;
    }

    public DateDeserializer(String format, Class<?> ownerType, String property) {
        this(format, property);
        this.ownerType = ownerType;
    }

    public DateDeserializer(String format) {
        this(Date.class, LocalDate.class, LocalDateTime.class, ZonedDateTime.class);
        this.format = DateTimeFormatter.ofPattern(format);
    }

    private DateDeserializer(Class<?>... types) {
        this.types = Arrays.asList(types);
    }

    @Override
    public boolean accept(Class<?> owner, Class<?> type, String property) {
        return types.contains(type) &&
                (this.property == null || this.property.equals(property)) &&
                (this.ownerType == null || this.ownerType.isAssignableFrom(owner));
    }

    @Override
    public Object deserialize(Class<?> type, Object value) {
        if (Date.class.equals(type)) {
            return Date.from(LocalDateTime.parse(String.valueOf(value), format).atZone(ZoneId.systemDefault()).toInstant());
        } else if (LocalDate.class.equals(type)) {
            return LocalDate.parse(String.valueOf(value), format);
        } else if (LocalDateTime.class.equals(type)) {
            return LocalDateTime.parse(String.valueOf(value), format);
        } else if (ZonedDateTime.class.equals(type)) {
            return ZonedDateTime.parse(String.valueOf(value), format);
        } else {
            throw new ValueDeserializationException("Unsupported type "+type);
        }
    }

}
