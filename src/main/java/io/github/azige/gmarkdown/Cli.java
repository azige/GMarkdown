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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.*;

/**
 *
 * @author Azige
 */
public class Cli{

    public static void main(String[] args){
        
        Options options = new Options()
            .addOption("h", "help", false, "print this message")
            .addOption(OptionBuilder
                .hasArg()
                .withArgName("bundle")
                .withDescription("set the ResourceBundle")
                .create('r')
            )
            .addOption(OptionBuilder
                .hasArg()
                .withArgName("locale")
                .withDescription("set the locale")
                .create('l'))
            .addOption(OptionBuilder
                .hasArg()
                .withArgName("template")
                .withDescription("set the template")
                .create('t'));
        try{
            CommandLineParser parser = new BasicParser();
            CommandLine cl = parser.parse(options, args);

            if (cl.hasOption('h')){
                printHelp(System.out, options);
                return;
            }

            String[] fileArgs = cl.getArgs();
            if (fileArgs.length < 1){
                throw new ParseException("Missing input files.");
            }
            GMarkdown gm = new GMarkdown();
            if (cl.hasOption('t')){
                String templateArg = cl.getOptionValue('t');
                InputStream template = Cli.class.getResourceAsStream(templateArg);
                if (template == null){
                    template = new FileInputStream(templateArg);
                }
                gm.template(template);
            }

            String resource = cl.getOptionValue('r');
            if (resource != null){
                String locale = cl.getOptionValue('l');
                if (locale == null){
                    gm.resource(ResourceBundle.getBundle(resource));
                }else{
                    String[] strs = locale.split("_");
                    Locale.Builder builder = new Locale.Builder();
                    builder.setLanguage(strs[0]);
                    if (strs.length > 1){
                        builder.setRegion(strs[1]);
                    }
                    gm.resource(ResourceBundle.getBundle(resource, builder.build()));
                }
            }

            List<File> fileList = new LinkedList<>();
            for (String fileArg : fileArgs){
                if (fileArg.contains("*")){
                    fileArg = fileArg.replaceAll("\\.", "\\.").replaceAll("\\*", ".*");
                    File parent = new File(fileArg).getParentFile();
                    if (parent == null){
                        parent = new File(".");
                    }
                    final Pattern p = Pattern.compile(new File(fileArg).getName());
                    FileFilter filter = new FileFilter(){

                        @Override
                        public boolean accept(File pathname){
                            return p.matcher(pathname.getName()).matches();
                        }
                    };
                    File[] files = parent.listFiles(filter);
                    if (files != null){
                        fileList.addAll(Arrays.asList(files));
                    }
                }else{
                    fileList.add(new File(fileArg));
                }
            }

            for (File f : fileList){
                String result;
                try (Reader input = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))){
                    result = gm.process(input);
                }
                if (f.getName().contains(".")){
                    Matcher matcher = Pattern.compile(".+\\.").matcher(f.getName());
                    matcher.find();
                    f = new File(f.getParent(), matcher.group() + "html");
                }else{
                    f = new File(f.getParent(), f.getName() + ".html");
                }
                try (Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))){
                    output.write(result);
                }
            }
        }catch (ParseException ex){
            System.err.println(ex.getMessage());
            printHelp(System.err, options);
        }catch (IOException ex){
            System.err.println(ex);
        }

        //gm.resource("TestResource").template(Main.class.getResourceAsStream("template.html")).proccess(new File[]{new File(args[0])});
    }

    static void printHelp(PrintStream out, Options options){
        HelpFormatter hf = new HelpFormatter();
        PrintWriter pw = new PrintWriter(out);
        hf.printHelp(pw, hf.getWidth(), "gmarkdown [-r <bundle> [-l <locale>]] [-t <template>] <input files>",
            "Convert input files.", options, hf.getLeftPadding(), hf.getDescPadding(), null);
        pw.flush();
    }
}
