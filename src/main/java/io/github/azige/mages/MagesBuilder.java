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
package io.github.azige.mages;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import groovy.lang.Binding;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 *
 * @author Azige
 */
public class MagesBuilder{

    final ScriptEngine engine;
    final Bindings globalBind;
    final Map<String, Object> properties;
    String template;
    String strings;
    List<Plugin> plugins = new LinkedList<>();
    List<Filter> preFilters = new LinkedList<>();
    Filter htmlFilter;
    List<Filter> postFilters = new LinkedList<>();

    public MagesBuilder(){
        engine = new ScriptEngineManager().getEngineByName("groovy");
        globalBind = engine.createBindings();
        engine.setBindings(globalBind, ScriptContext.GLOBAL_SCOPE);
        properties = new HashMap<>();
    }

    public MagesBuilder setTemplate(String template){
        this.template = template;
        return this;
    }

    public MagesBuilder setStrings(String strings){
        this.strings = strings;
        return this;
    }

    public MagesBuilder addPreFilter(Filter filter){
        preFilters.add(filter);
        return this;
    }

    public MagesBuilder addPostFilter(Filter filter){
        postFilters.add(filter);
        return this;
    }

    public MagesBuilder addPlugin(Plugin plugin){
        plugins.add(plugin);
        globalBind.put(plugin.getName(), plugin);
        return this;
    }

    public MagesBuilder addProperty(String name, Object value){
        properties.put(name, value);
        return this;
    }

    public Mages build(){
        addPreFilter(new GroovyPreFilter());
        if (htmlFilter == null){
            htmlFilter = new MarkdownFilter();
        }
        Mages gm = new Mages(engine.getFactory(), globalBind, preFilters, htmlFilter, postFilters, properties);
        globalBind.put("mages", Util.wrapPlugin("mages", gm));
        for (Plugin p : plugins){
            if (p instanceof ScriptPlugin){
                ((ScriptPlugin)p).setBinding(new Binding(new HashMap(globalBind)));
            }
        }
        return gm;
    }
}
