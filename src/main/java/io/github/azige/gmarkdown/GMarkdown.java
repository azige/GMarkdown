/* * Copyright 2014 Azige. *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.azige.gmarkdown;

import java.util.List;

import javax.script.*;

/**
 *
 * @author Azige
 */
public class GMarkdown{

    final ScriptEngine engine;
    final List<Filter> preFilters;
    final Filter htmlFilter;
    final List<Filter> postFilters;

    GMarkdown(ScriptEngine engine, List<Filter> preFilters, Filter htmlFilter, List<Filter> postFilters){
        this.engine = engine;
        this.preFilters = preFilters;
        this.htmlFilter = htmlFilter;
        this.postFilters = postFilters;
    }

    public String process(String source){
        engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        for (Filter f : preFilters){
            source = f.filter(source);
        }
        source = htmlFilter.filter(source);
        for (Filter f : postFilters){
            source = f.filter(source);
        }
        return source;
    }
}
