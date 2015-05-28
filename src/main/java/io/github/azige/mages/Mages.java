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

import java.util.List;
import java.util.Map;

import javax.script.*;

/**
 *
 * @author Azige
 */
public class Mages{

    final ScriptEngineFactory engineFactory;
    final Bindings globalBind;
    final List<Filter> preFilters;
    final Filter htmlFilter;
    final List<Filter> postFilters;
    final Map<String, Object> properties;

    Mages(
        ScriptEngineFactory engineFactory,
        Bindings globalBind,
        List<Filter> preFilters,
        Filter htmlFilter,
        List<Filter> postFilters,
        Map<String, Object> properties
    ){
        this.engineFactory = engineFactory;
        this.globalBind = globalBind;
        this.preFilters = preFilters;
        this.htmlFilter = htmlFilter;
        this.postFilters = postFilters;
        this.properties = properties;
    }

    public String process(String source){
        ScriptEngine engine = engineFactory.getScriptEngine();
        engine.setBindings(globalBind, ScriptContext.GLOBAL_SCOPE);
        for (Filter f : preFilters){
            if (f instanceof GroovyFilter){
                ((GroovyFilter)f).setEngine(engine);
            }
            source = f.filter(source);
        }
        source = htmlFilter.filter(source);
        for (Filter f : postFilters){
            if (f instanceof GroovyFilter){
                ((GroovyFilter)f).setEngine(engine);
            }
            source = f.filter(source);
        }
        return source;
    }

    public Object getProperty(String name){
        return properties.get(name);
    }

    public void setProperty(String name, Object value){
        properties.put(name, value);
    }
}
