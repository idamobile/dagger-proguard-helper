Dagger Proguard Helper
======================

Generates proguard config file with `-keepnames` for all classes required by [Dagger](https://github.com/square/dagger).


Usage
---

You should add to your pom.xml:

        <dependency>
            <groupId>com.github.idamobile</groupId>
            <artifactId>dagger-proguard-helper-processor</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>

Also add to your `<repositories>`:

        <repository>
            <id>ida-repo-public</id>
            <url>http://nexus.idamob.ru/content/groups/public/</url>
        </repository>

The Dagger Proguard Helper annotation processor will generate `dagger-proguard-keepnames.cfg` at root of your project. You have to add this config file to proguard configuration. If you are using [android-maven-plugin](https://code.google.com/p/maven-android-plugin/) the configuration should be like following:

        <plugin>
            <groupId>com.jayway.maven.plugins.android.generation2</groupId>
            <artifactId>android-maven-plugin</artifactId>
            <configuration>
               <proguard>
                  <config>proguard.cfg</config>
                  <configs>
                      <config>${env.ANDROID_HOME}/tools/proguard/proguard-android.txt</config>
                      <config>dagger-proguard-keepnames.cfg</config>
                  </configs>
               </proguard>
            </configuration>
        </plugin>

The recommended section for Dagger in your `proguard.cfg`:

        # Dagger
        -dontwarn dagger.internal.codegen.**
        -keepclassmembers,allowobfuscation class * {
            @javax.inject.* *;
            @dagger.* *;
            <init>();
        }
        -keep class dagger.* { *; }
        -keep class javax.inject.* { *; }
        -keep class * extends dagger.internal.Binding
        -keep class * extends dagger.internal.ModuleAdapter
        -keep class * extends dagger.internal.StaticInjection


Licence
=======
  
             Copyright 2013 iDa Mobile.
        
           Licensed under the Apache License, Version 2.0 (the "License");
           you may not use this file except in compliance with the License.
           You may obtain a copy of the License at
        
               http://www.apache.org/licenses/LICENSE-2.0
        
           Unless required by applicable law or agreed to in writing, software
           distributed under the License is distributed on an "AS IS" BASIS,
           WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
           See the License for the specific language governing permissions and
           limitations under the License.
