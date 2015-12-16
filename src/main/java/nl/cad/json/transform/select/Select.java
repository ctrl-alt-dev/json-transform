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
package nl.cad.json.transform.select;

import java.util.List;
import java.util.Map;

import nl.cad.json.transform.path.Path;

/**
 * allows the matching of a selection against a source.
 */
public interface Select {

    /**
     * matches the given source against this selection.
     * @param source the source.
     * @return all matching paths and their values.
     */
    Map<Path, Object> select(Object source);

    /**
     * matches the given source against this selection, expecting only one match.
     * @param source the source.
     * @return the matching path and value.
     */
    Map.Entry<Path, Object> selectOne(Map<String, Object> source);

    /**
     * matches the given source against this selection.
     * @param source the source.
     * @return all matching paths.
     */
    List<Path> selectPaths(Map<String, Object> source);

    /**
     * matches the given source against this selection, expecting only one match.
     * @param source the source.
     * @return the matching path.
     */
    Path selectOnePath(Map<String, Object> source);

}
