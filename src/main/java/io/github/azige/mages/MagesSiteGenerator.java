/*
 * Copyright 2015 Azige.
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Azige
 */
public class MagesSiteGenerator{

    public static final String DEFAULT_PLUGIN_DIR = "plugin";
    public static final String DEFAULT_TEMPLATE = "template.html";
    public static final Pattern FILE_NAME_PATTERN = Pattern.compile("(.*)(\\..*)?");
    public static final String SOURCE_FILE_SUFFIX = ".gmd";

    File sourceDir;
    File targetDir;
    File pluginDir;
    File template;
    File resource;
    boolean force = false;
    List<Plugin> plugins = new ArrayList<>();
    Queue<Task> tasks = new LinkedList<>();
    Set<File> sourceSet = new HashSet<>();

    class Task{

        File source;
        File target;

        public Task(File source, File target){
            this.source = source;
            this.target = target;
        }
    }

    public MagesSiteGenerator(File sourceDir, File targetDir){
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
        pluginDir = new File(sourceDir, DEFAULT_PLUGIN_DIR);
        plugins.add(Util.loadPlugin("io.github.azige.mages.Strings"));
    }

    public File getSourceDir(){
        return sourceDir;
    }

    public File getTargetDir(){
        return targetDir;
    }

    public File getPluginDir(){
        return pluginDir;
    }

    public void setPluginDir(File pluginDir){
        this.pluginDir = pluginDir;
    }

    public boolean isForce(){
        return force;
    }

    public void setForce(boolean force){
        this.force = force;
    }

    public File getTemplate(){
        return template;
    }

    public void setTemplate(File template){
        this.template = template;
    }

    public File getResource(){
        return resource;
    }

    public void setResource(File resource){
        this.resource = resource;
    }

    public void addTask(String path){
        File source = new File(sourceDir, path);
        File target = new File(targetDir, path);
        if (sourceSet.contains(source)){
            return;
        }
        sourceSet.add(source);
        if (source.isDirectory()){
            for (File f : source.listFiles(file -> file.getName().endsWith(SOURCE_FILE_SUFFIX))){
                String targetName = f.getName().substring(0, f.getName().length() - SOURCE_FILE_SUFFIX.length()) + ".html";
                tasks.offer(new Task(f, new File(target, targetName)));
            }
        }else{
            if (source.getName().endsWith(SOURCE_FILE_SUFFIX)){
                String targetName = source.getName().substring(0, source.getName().length() - SOURCE_FILE_SUFFIX.length()) + ".html";
                tasks.offer(new Task(source, new File(target.getParent(), targetName)));
            }else{
                tasks.offer(new Task(source, new File(target.getParent(), source.getName() + ".html")));
            }
        }
    }

    public void start(){
        try{
            for (Plugin p : Util.loadPluginsFromDirectory(pluginDir)){
                plugins.add(p);
            }

            MagesBuilder builder = new MagesBuilder();

            for (Plugin p : plugins){
                p.onStart(this);
                builder.addPlugin(p);
            }

            if (template == null){
                template = new File(DEFAULT_TEMPLATE);
            }
            if (template.exists()){
                builder.addPostFilter(new TemplateFilter(FileUtils.readFileToString(template, "UTF-8")));
            }else{
                builder.addPostFilter(new TemplateFilter());
            }

            if (force){
                builder.addProperty("force", true);
            }

            Mages mages = builder.build();

            while (!tasks.isEmpty()){
                Task task = tasks.poll();
                mages.setProperty("currentFile", task.source);
                if (task.source.lastModified() > task.target.lastModified()){
                    String source = FileUtils.readFileToString(task.source, "UTF-8");
                    FileUtils.writeStringToFile(task.target, mages.process(source), "UTF-8");
                    System.out.println(task.source.getName() + " -> " + task.target.getName());
                }else{
                    System.out.println(task.source.getName() + " passed.");
                }
            }

            for (Plugin p : plugins){
                p.onDestroy(this);
            }
        }catch (IOException ex){
            throw new MagesException(ex);
        }
    }
}
