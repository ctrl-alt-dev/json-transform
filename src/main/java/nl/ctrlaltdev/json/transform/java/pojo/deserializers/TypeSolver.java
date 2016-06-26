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
package nl.ctrlaltdev.json.transform.java.pojo.deserializers;

import java.util.Map;
import java.util.Set;

public interface TypeSolver {

    /**
     * determines the subclass of the declared class that this object represents.
     * @param declared the declared class.
     * @param object the object.
     * @param handled the map of handled properties.
     * @return the actual (sub) class.
     */
    <A> Class<? extends A> solveType(Class<A> declared, Map<String, Object> object, Set<String> handled);

}
