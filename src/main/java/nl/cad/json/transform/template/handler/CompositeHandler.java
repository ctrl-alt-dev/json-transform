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
package nl.cad.json.transform.template.handler;

import java.util.Map;

import nl.cad.json.transform.path.Path;
import nl.cad.json.transform.template.CallbackHandler;

public class CompositeHandler implements CallbackHandler {

    public static class UnsupportedHandlerException extends RuntimeException {
        public UnsupportedHandlerException(String msg) {
            super(msg);
        }
    }

    private CallbackHandler[] delegates;

    public CompositeHandler(CallbackHandler... delegates) {
        this.delegates = delegates;
    }

    @Override
    public Object handle(Path path, Object value, Map<String, Object> source) {
        for (CallbackHandler h : delegates) {
            if (h.supports(value)) {
                return h.handle(path, value, source);
            }
        }
        throw new UnsupportedHandlerException(String.valueOf(value));
    }

    @Override
    public boolean supports(Object value) {
        for (CallbackHandler h : delegates) {
            if (h.supports(value)) {
                return true;
            }
        }
        return false;
    }

}
