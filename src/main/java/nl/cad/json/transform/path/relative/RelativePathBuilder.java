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

public class RelativePathBuilder {

    public static RelativePathBuilder relativePath() {
        return new RelativePathBuilder();
    }

    private RelativePath current = new NoMove();

    public RelativePathBuilder parent() {
        current = new ParentMove(current);
        return this;
    }

    public RelativePathBuilder index(int idx) {
        current = new IndexMove(current, idx);
        return this;
    }

    public RelativePathBuilder property(String property) {
        current = new PropertyMove(current, property);
        return this;
    }

    public RelativePath build() {
        return current;
    }

}
