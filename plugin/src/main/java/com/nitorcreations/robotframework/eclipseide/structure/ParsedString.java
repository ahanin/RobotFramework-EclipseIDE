/**
 * Copyright 2012 Nitor Creations Oy
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
package com.nitorcreations.robotframework.eclipseide.structure;

import com.nitorcreations.robotframework.eclipseide.internal.rules.ArgumentUtils;
import com.nitorcreations.robotframework.eclipseide.structure.api.IParsedKeywordString;

/**
 * An immutable implementation of all the I*String interfaces in the ...builder.info package.
 * 
 * @author xkr47
 */
public class ParsedString implements IParsedKeywordString {

    private static String[] STRIPPABLE_PREFIXES = { "given ", "when ", "then ", "and " };

    private final String value;
    private final int argCharPos;
    private ArgumentType type = ArgumentType.IGNORED;
    private boolean hasSpaceAfter;

    public enum ArgumentType {
        IGNORED, COMMENT, TABLE, SETTING_KEY, VARIABLE_KEY, NEW_TESTCASE, NEW_KEYWORD, SETTING_VAL, SETTING_FILE, SETTING_FILE_WITH_NAME_KEY, SETTING_FILE_ARG, SETTING_FILE_WITH_NAME_VALUE, VARIABLE_VAL, KEYWORD_LVALUE, FOR_PART, KEYWORD_CALL, KEYWORD_CALL_DYNAMIC, KEYWORD_ARG,
    }

    public ParsedString(String value, int argCharPos) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        if (argCharPos < 0) {
            throw new IllegalArgumentException("argCharPos < 0");
        }
        this.value = value;
        this.argCharPos = argCharPos;
    }

    @Override
    public String getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value.isEmpty() || value.equals("\\");
    }

    @Override
    public int getArgCharPos() {
        return argCharPos;
    }

    @Override
    public int getArgEndCharPos() {
        return argCharPos + value.length();
    }

    // if argument is followed by a space, this extends the endCharPos to include that space
    public int getExtendedArgEndCharPos() {
        return getArgEndCharPos() + (hasSpaceAfter ? 1 : 0);
    }

    @Override
    public String getAlternateValue() {
        String lcValue = value.toLowerCase();
        for (String strippablePrefix : STRIPPABLE_PREFIXES) {
            if (lcValue.startsWith(strippablePrefix)) {
                return value.substring(strippablePrefix.length());
            }
        }
        return null;
    }

    public ArgumentType getType() {
        return type;
    }

    public ParsedString setType(ArgumentType type) {
        assert type != null;
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return '"' + value + "\" (" + type + ')';
    }

    @Override
    public String getDebugString() {
        return toString() + " @" + argCharPos + "-" + (getArgEndCharPos() - 1);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + argCharPos;
        result = prime * result + (hasSpaceAfter ? 1231 : 1237);
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ParsedString other = (ParsedString) obj;
        if (argCharPos != other.argCharPos)
            return false;
        if (hasSpaceAfter != other.hasSpaceAfter)
            return false;
        if (type != other.type)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public DynamicParsedString splitRegularArgument() {
        // TODO implement
        return new DynamicParsedString(value, argCharPos, null);
    }

    public String getUnescapedValue() {
        return ArgumentUtils.unescapeArgument(value, 0, value.length());
    }

    public void setHasSpaceAfter(boolean hasSpaceAfter) {
        this.hasSpaceAfter = hasSpaceAfter;
    }

    public boolean hasSpaceAfter() {
        return hasSpaceAfter;
    }

    public void copyTypeVariablesFrom(ParsedString source) {
        setType(source.getType());
    }

}
