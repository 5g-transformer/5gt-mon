/*
* Copyright 2018 Nextworks s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package it.nextworks.nfvmano.configmanager.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Marco Capitani on 28/09/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);

    private Properties properties;

    public String getProperty(String key) {
        if (properties == null) {
            try {
                init();
            } catch (IOException ex) {
                log.error(ex.getMessage());
                return null;
            }
        }
        return properties.getProperty(key);
    }

    public int getIntProperty(String key) {
        return Integer.valueOf(getProperty(key));
    }

    public double getFloatProperty(String key) {
        return Double.valueOf(getProperty(key));
    }

    public boolean getBoolProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    private void init() throws IOException {

        InputStream inputStream;
        String propFileName = "config.properties";

        File file = new File(propFileName);

        if (file.exists() && file.isFile()) {
            inputStream = new FileInputStream(file);
        } else {
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        }

        if (inputStream != null) {
            properties = new Properties();
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in cwd or the classpath");
        }
    }
}
