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

import java.util.ArrayList;
import java.util.List;

import nl.cad.json.transform.select.parse.SelectTokenizer;
import nl.cad.json.transform.select.parse.Token;
import nl.cad.json.transform.select.selector.AndSelector;
import nl.cad.json.transform.select.selector.AnyArraySelector;
import nl.cad.json.transform.select.selector.AnyObjectSelector;
import nl.cad.json.transform.select.selector.AnyValueOfTypeSelector;
import nl.cad.json.transform.select.selector.AnyValueSelector;
import nl.cad.json.transform.select.selector.ExorSelector;
import nl.cad.json.transform.select.selector.IndexSelector;
import nl.cad.json.transform.select.selector.NotSelector;
import nl.cad.json.transform.select.selector.ObjectPropertyMatchSelector;
import nl.cad.json.transform.select.selector.ObjectPropertySelector;
import nl.cad.json.transform.select.selector.ObjectPropertyValueSelector;
import nl.cad.json.transform.select.selector.OrSelector;
import nl.cad.json.transform.select.selector.PropertyMatchSelector;
import nl.cad.json.transform.select.selector.PropertySelector;
import nl.cad.json.transform.select.selector.RootSelector;
import nl.cad.json.transform.select.selector.Selector;
import nl.cad.json.transform.select.selector.WildcardSelector;

public class SelectBuilder {

    public static SelectBuilder select() {
        return new SelectBuilder();
    }

    public static Select selectRoot() {
        return select().root().build();
    }

    private final List<Selector> selectors = new ArrayList<Selector>();
    private boolean one = false;

    protected SelectBuilder() {
    }

    public SelectBuilder one() {
        one = true;
        return this;
    }

    public SelectBuilder any() {
        selectors.add(new WildcardSelector());
        return this;
    }

    public SelectBuilder anyArray() {
        selectors.add(new AnyArraySelector());
        return this;
    }

    public SelectBuilder anyObject() {
        selectors.add(new AnyObjectSelector());
        return this;
    }

    public SelectBuilder anyValue() {
        selectors.add(new AnyValueSelector());
        return this;
    }

    public SelectBuilder anyValueOfType(Class<?> type) {
        selectors.add(new AnyValueOfTypeSelector(type));
        return this;
    }

    public SelectBuilder property(String property) {
        selectors.add(new PropertySelector(property));
        return this;
    }

    public SelectBuilder propertyMatch(String regex) {
        selectors.add(new PropertyMatchSelector(regex));
        return this;
    }

    public SelectBuilder objectProperty(String property) {
        selectors.add(new ObjectPropertySelector(property));
        return this;
    }

    public SelectBuilder objectPropertyMatch(String regex) {
        selectors.add(new ObjectPropertyMatchSelector(regex));
        return this;
    }

    public SelectBuilder objectPropertyValue(String property, String value) {
        selectors.add(new ObjectPropertyValueSelector(property, value));
        return this;
    }

    public SelectBuilder index(int index) {
        selectors.add(new IndexSelector(index));
        return this;
    }

    public SelectBuilder and(SelectBuilder builder) {
        selectors.add(new AndSelector(builder.buildComposite()));
        return this;
    }

    public SelectBuilder or(SelectBuilder builder) {
        selectors.add(new OrSelector(builder.buildComposite()));
        return this;
    }

    public SelectBuilder exor(SelectBuilder builder) {
        selectors.add(new ExorSelector(builder.buildComposite()));
        return this;
    }

    public SelectBuilder custom(Selector selector) {
        selectors.add(selector);
        return this;
    }

    public SelectBuilder not(Selector selector) {
        selectors.add(new NotSelector(selector));
        return this;
    }

    public SelectBuilder root() {
        selectors.add(new RootSelector());
        return this;
    }

    public Select build() {
        return one ? new SingleResultSelectorChain(buildComposite()) : new SelectorChain(buildComposite());
    }

    private Selector[] buildComposite() {
        return selectors.toArray(new Selector[selectors.size()]);
    }

    public static Select fromString(String str) {
        SelectBuilder builder = select();
        for (Token t : SelectTokenizer.tokenize(str)) {
            t.invoke(builder);
        }
        return builder.build();
    }

}
