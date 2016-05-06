/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */

package cn.vlabs.duckling.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Introduction Here.
 * @date Mar 3, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class SqlUtils
{
    private SqlUtils(){}
    private static boolean isDelimiter(String line){
        String newLine = line.toLowerCase();
        if (newLine.startsWith("delimiter"))
            return true;
        else
            return false;
    }
    private static String parseDelimiter(String line){
        String newLine= line.toLowerCase();
        String result = newLine.replace("delimiter", "");
        return result.trim();
    }
    public static List<String> getSqls(String fileLocation){
        if(!new File(fileLocation).exists()){
            return new ArrayList<String>();
        }
        BufferedReader reader=null;
        List<String> sqlList = new ArrayList<String>();
        String delimiter=";";
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileLocation),"UTF-8"));
            String line = null;
            StringBuffer sql=new StringBuffer();
            while ((line=reader.readLine())!=null){
                line = line.trim();
                
                if (line.length()!=0){
                    if (isDelimiter(line)){
                        delimiter=parseDelimiter(line);
                        continue;
                    }
                    sql.append("\n");
                    if (line.endsWith(delimiter)){
                        line =line.replace(delimiter, "");
                        sql.append(line);
                        sqlList.add(sql.toString());
                        sql= new StringBuffer();
                    }else{
                        sql.append(line);
                    }
                }

            }
            if (sql.length()>0){
                sqlList.add(sql.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            if (reader!=null)
                try {
                    reader.close();
                } catch (IOException e) {
                }
        }
        return sqlList;
    }

}