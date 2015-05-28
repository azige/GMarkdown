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

import java.io.*;

import org.apache.commons.cli.*;

/**
 *
 * @author Azige
 */
public class Cli{

    public static void main(String[] args){

        Options options = new Options()
            .addOption("h", "help", false, "print this message")
            .addOption(Option.builder("r")
                .hasArg()
                .argName("bundle")
                .desc("set the ResourceBundle")
                .build()
            )
            .addOption(Option.builder("l")
                .hasArg()
                .argName("locale")
                .desc("set the locale")
                .build()
            )
            .addOption(Option.builder("t")
                .hasArg()
                .argName("template")
                .desc("set the template")
                .build()
            )
            .addOption(Option.builder("p")
                .hasArg()
                .argName("plugin dir")
                .desc("set the directory to load plugins")
                .build()
            )
            .addOption(Option.builder("f")
                .desc("force override existed file")
                .build()
            );
        try{
            CommandLineParser parser = new DefaultParser();
            CommandLine cl = parser.parse(options, args);

            if (cl.hasOption('h')){
                printHelp(System.out, options);
                return;
            }

            MagesSiteGenerator msg = new MagesSiteGenerator(new File("."), new File("."));

            String[] fileArgs = cl.getArgs();
            if (fileArgs.length < 1){
                msg.addTask(".");
            }else{
                for (String path : fileArgs){
                    msg.addTask(path);
                }
            }

            if (cl.hasOption("t")){
                msg.setTemplate(new File(cl.getOptionValue("t")));
            }

            if (cl.hasOption("r")){
                msg.setResource(new File(cl.getOptionValue("r")));
            }else{
                File resource = new File("Resource.properties");
                if (resource.exists()){
                    msg.setResource(resource);
                }
            }

            if (cl.hasOption("p")){
                msg.setPluginDir(new File(cl.getOptionValue("p")));
            }

            if (cl.hasOption("f")){
                msg.setForce(true);
            }

            msg.start();
        }catch (ParseException ex){
            System.err.println(ex.getMessage());
            printHelp(System.err, options);
        }
    }

    static void printHelp(PrintStream out, Options options){
        HelpFormatter hf = new HelpFormatter();
        PrintWriter pw = new PrintWriter(out);
        hf.printHelp(pw, hf.getWidth(), "mages [-r <bundle> [-l <locale>]] [-t <template>] [-p <plugin dir>] <input files>",
            "Convert input files.", options, hf.getLeftPadding(), hf.getDescPadding(), null);
        pw.flush();
    }
}
