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

/**
 *
 * @author Azige
 */
public class Strings implements Plugin{

    private ResourceBundle bundle

    Strings(){
        def res = System.getProperty("strings.resource")
        def locale = System.getProperty("strings.locale")
        if (res){
            if (!locale){
                bundle = ResourceBundle.getBundle(res)
            }else{
                def strs = locale.split("_")
                def builder = new Locale.Builder()
                builder.setLanguage(strs[0])
                if (strs.length > 1){
                    builder.setRegion(strs[1]);
                }
                bundle = ResourceBundle.getBundle(res, builder.build())
            }
        }
    }

    String getName(){
        "strings"
    }

    String getProperty(String key){
        bundle ? bundle.getString(key) : ""
    }
}