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

package io.github.azige.mages

/**
 *
 * @author Azige
 */
class PluginWrapper implements ScriptPlugin{

    String name
    Object plugin
    boolean isScript

    PluginWrapper(String name, Object plugin){
        this.name = name
        this.plugin = plugin
        this.isScript = plugin instanceof Script
    }

    void setBinding(Binding bind){
        if (isScript){
            plugin.setBinding(bind)
        }
    }

    void onStart(MagesSiteGenerator m){
        if (plugin.class.methods.find{it.name == 'onStart'}){
            plugin.onStart(m)
        }
    }

    void onDestroy(MagesSiteGenerator m){
        if (plugin.class.methods.find{it.name == 'onDestroy'}){
            plugin.onDestroy(m)
        }
    }

    def getProperty(String property){
        if (property == "name"){
            return name
        }else{
            return plugin.getProperty(property)
        }
    }

    void setProperty(String property, def value){
        plugin.setProperty(property, value)
    }

    def invokeMethod(String method, def args){
        plugin.invokeMethod(method, args)
    }

    String toString(){
        isScript ? plugin.run() : plugin
    }
}
