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

import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Azige
 */
public class GMarkdown{

    InputStream template;
    String locale;
    String resource;

    public GMarkdown template(InputStream template){
        this.template = template;
        return this;
    }

    public GMarkdown locale(String locale){
        this.locale = locale;
        return this;
    }

    public GMarkdown resource(String resource){
        this.resource = resource;
        return this;
    }

    public void proccess(File[] files){
        try{
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
            Strings strings;
            if (locale == null){
                strings = new Strings(ResourceBundle.getBundle(resource));
            }else{
                String[] strs = locale.split("_");
                Locale.Builder builder = new Locale.Builder();
                builder.setLanguage(strs[0]);
                if (strs.length > 1){
                    builder.setRegion(strs[1]);
                }
                strings = new Strings(ResourceBundle.getBundle(resource, builder.build()));
            }
            engine.getBindings(ScriptContext.GLOBAL_SCOPE).put("strings", strings);
            for (File f : files){
                engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
                String result = engine.eval(new FileReader(f)).toString();
                GithubAPI api = new GithubAPI();
                result = api.convertMarkdown(result);
                engine.put("body", result);
                StringBuilder sb = new StringBuilder();
                sb.append("\"\"\"");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(template, "UTF-8"))){
                    String line;
                    while ((line = in.readLine()) != null){
                        sb.append(line).append('\n');
                    }
                }
                sb.append("\"\"\"");
                result = engine.eval(sb.toString()).toString();
                System.out.println(result);
            }
        }catch (IOException | ScriptException ex){
            throw new RuntimeException(ex);
        }
    }
}
