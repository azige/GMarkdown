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

import java.io.*;
import java.util.ResourceBundle;

import javax.script.*;

/**
 *
 * @author Azige
 */
public class GMarkdown{

    static final String DEFAULT_TEMPLATE = "template.html";

    final ScriptEngine engine;
    String template;

    public GMarkdown(){
        engine = new ScriptEngineManager().getEngineByName("groovy");
        engine.setBindings(engine.createBindings(), ScriptContext.GLOBAL_SCOPE);
        bindStrings(null);
    }

    public GMarkdown template(InputStream template){
        if (template == null){
            throw new NullPointerException();
        }
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        try (BufferedReader in = new BufferedReader(new InputStreamReader(template, "UTF-8"))){
            for (int c; (c = in.read(buffer)) != -1;){
                sb.append(buffer, 0, c);
            }
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
        this.template = sb.toString();
        return this;
    }

    private void readDefaultTemplate(){
        template(GMarkdown.class.getResourceAsStream("template.html"));
    }

    public GMarkdown resource(ResourceBundle bundle){
        bindStrings(bundle);
        return this;
    }

    private void bindStrings(ResourceBundle bundle){
        engine.getBindings(ScriptContext.GLOBAL_SCOPE).put("strings", new Strings(bundle));
    }

    public String process(Reader input){
        try{
            engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
            String result = engine.eval(input).toString();
            GithubApi api = new GithubApi();
            result = api.convertMarkdown(result);
            engine.put("content", result);
            StringBuilder sb = new StringBuilder();
            sb.append("\"\"\"");
            if (template == null){
                readDefaultTemplate();
            }
            sb.append(template);
            sb.append("\"\"\"");
            result = engine.eval(sb.toString()).toString();
            return result;
        }catch (IOException | ScriptException ex){
            throw new RuntimeException(ex);
        }
    }
}
