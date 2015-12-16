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
package nl.cad.json.transform.select.parse;

import java.util.ArrayList;
import java.util.List;

public final class SelectTokenizer {

    private SelectTokenizer() {
    }

    public static List<Token> tokenize(String str) {
        List<Token> tokens = new ArrayList<>();
        boolean inQuote = false;
        StringBuilder currentToken = new StringBuilder();
        Token token = new Token();
        char prevChar = 0;
        for (int t = 0; t < str.length(); t++) {
            char c = str.charAt(t);
            if (c == '"') {
                inQuote = !inQuote;
                if (prevChar == '"') {
                    currentToken.append(prevChar);
                }
            } else if (!inQuote) {
                if (c == '(') {
                    token.setMethod(currentToken.toString());
                    currentToken.delete(0, currentToken.length());
                } else if (c == ')' || (c == ',')) {
                    if (currentToken.length() > 0) {
                        token.addArg(currentToken.toString());
                        currentToken.delete(0, currentToken.length());
                    }
                } else if (c == '.') {
                    tokens.add(token);
                    token = new Token();
                    currentToken.delete(0, currentToken.length());
                } else {
                    currentToken.append(c);
                }
            } else {
                currentToken.append(c);
            }
            prevChar = c;
        }
        if (!token.isEmpty()) {
            tokens.add(token);
        }
        return tokens;
    }
}
