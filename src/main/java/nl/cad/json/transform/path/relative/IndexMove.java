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
package nl.cad.json.transform.path.relative;

import nl.cad.json.transform.path.Path;

public class IndexMove implements RelativePath {

    private final int idx;
    private final RelativePath parent;

    public IndexMove(int idx) {
        this(null, idx);
    }

    public IndexMove(RelativePath parent, int idx) {
        this.parent = parent;
        this.idx = idx;
    }

    @Override
    public Path apply(Path path) {
        if (parent != null) {
            path = parent.apply(path);
        }
        return path.enter(idx);
    }

}
