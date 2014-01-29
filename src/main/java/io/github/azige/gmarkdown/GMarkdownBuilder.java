/*
 * Copyright 2014 Azige.
 *
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

import java.util.LinkedList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 *
 * @author Azige
 */
public class GMarkdownBuilder{

    final ScriptEngine engine;
    final Bindings globalBind;
    String template;
    String strings;
    List<Filter> preFilters = new LinkedList<>();
    Filter htmlFilter;
    List<Filter> postFilters = new LinkedList<>();

    public GMarkdownBuilder(){
        engine = new ScriptEngineManager().getEngineByName("groovy");
        globalBind = engine.createBindings();
        engine.setBindings(globalBind, ScriptContext.GLOBAL_SCOPE);
    }

    public GMarkdownBuilder setTemplate(String template){
        this.template = template;
        return this;
    }

    public GMarkdownBuilder setStrings(String strings){
        this.strings = strings;
        return this;
    }

    public GMarkdownBuilder addPreFilter(Filter filter){
        if (filter instanceof GroovyFilter){
            ((GroovyFilter)filter).setEngine(engine);
        }
        preFilters.add(filter);
        return this;
    }

    public GMarkdownBuilder addPostFilter(Filter filter){
        if (filter instanceof GroovyFilter){
            ((GroovyFilter)filter).setEngine(engine);
        }
        postFilters.add(filter);
        return this;
    }

    public GMarkdownBuilder addPlugin(Plugin plugin){
        globalBind.put(plugin.getName(), plugin);
        return this;
    }

    public GMarkdown build(){
        addPreFilter(new GroovyPreFilter());
        if (htmlFilter == null){
            htmlFilter = new MarkdownFilter();
        }
        return new GMarkdown(engine, preFilters, htmlFilter, postFilters);
    }
}
