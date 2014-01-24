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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.*;

/**
 *
 * @author Azige
 */
public final class GithubApi{

    static final Logger LOG = Logger.getLogger(GithubApi.class.getName());

    private final static String ROOT = "https://api.github.com";

    static void enableLog(){
        Handler h = new ConsoleHandler();
        h.setLevel(Level.ALL);
        LOG.addHandler(h);
        LOG.setLevel(Level.ALL);
    }

    public String convertMarkdown(String markdown) throws IOException{
        final String URL = ROOT + "/markdown/raw";
        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection)new URL(URL).openConnection();
        }catch (MalformedURLException ex){
            throw new RuntimeException(ex);
        }
        conn.addRequestProperty("Content-Type", "text/x-markdown; charset=UTF-8");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        new PrintStream(conn.getOutputStream(), false, "UTF-8").print(markdown);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))){
            LOG.log(Level.FINEST, conn.getHeaderFields().toString());
            for (String s; (s = in.readLine()) != null;){
                sb.append(s).append('\n');
            }
        }
        return sb.toString();
    }
}
