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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import groovy.lang.GroovyClassLoader;

/**
 *
 * @author Azige
 */
final class Util{

    static final int BUFFER_SIZE = 1 << 16;

    private Util(){
    }

    static String readAll(Reader in) throws IOException{
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[BUFFER_SIZE];
        for (int b; (b = in.read(buffer)) != -1;){
            sb.append(buffer, 0, b);
        }
        return sb.toString();
    }

    static Plugin loadPlugin(String className){
        try{
            return (Plugin)Class.forName(className).newInstance();
        }catch (Exception ex){
            throw new MagesException(ex);
        }
    }

    static List<Plugin> loadPluginsFromDirectory(File dir){
        List<Plugin> list = new LinkedList<>();
        File[] files = dir.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name){
                return name.endsWith(".groovy");
            }
        });
        if (files == null){
            return list;
        }
        try{
            GroovyClassLoader loader = new GroovyClassLoader();
            loader.addClasspath(dir.getCanonicalPath());
            final int extLength = ".groovy".length();
            for (File f : files){
                String name = f.getName();
                list.add(wrapPlugin(name.substring(0, name.length() - extLength), loader.parseClass(f).newInstance()));
            }
        }catch (Exception ex){
            throw new MagesException(ex);
        }
        return list;
    }

    static Plugin wrapPlugin(String name, Object plugin){
        try{
            Constructor<?> constructor = Class.forName("io.github.azige.mages.PluginWrapper").getConstructor(String.class, Object.class);
            return (Plugin)constructor.newInstance(name, plugin);
        }catch (Exception ex){
            throw new MagesException(ex);
        }
    }
}
