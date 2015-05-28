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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 *
 * @author Azige
 */
public class TemplateFilter implements GroovyFilter{

    static final String DEFAULT_TEMPLATE = "template.html";

    ScriptEngine engine;
    String template;

    public TemplateFilter(){
    }

    public TemplateFilter(String template){
        this.template = "\"\"\"" + template + "\"\"\"";
    }

    public void setTemplate(String template){
        this.template = "\"\"\"" + template + "\"\"\"";
    }

    @Override
    public void setEngine(ScriptEngine engine){
        this.engine = engine;
    }

    @Override
    public String filter(String source){
        if (template == null){
            loadDefaultTemplate();
        }
        engine.put("content", source);
        try{
            return engine.eval(template).toString();
        }catch (ScriptException ex){
            throw new MagesException(ex);
        }
    }

    private void loadDefaultTemplate(){
        try (Reader in = new InputStreamReader(TemplateFilter.class.getResourceAsStream(DEFAULT_TEMPLATE), "UTF-8")){
            setTemplate(Util.readAll(in));
        }catch(IOException ex){
            throw new MagesException(ex);
        }
    }
}
