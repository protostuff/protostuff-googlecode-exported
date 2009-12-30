//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.protostuff.protoparser;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * TODO
 *
 * @author David Yu
 * @created Dec 18, 2009
 */
public class Proto
{
    
    final File file;
    final Loader loader;
    final Proto importer;
    String packageName, javaPackageName;
    final LinkedHashMap<String, Proto> importedProtos = new LinkedHashMap<String, Proto>();
    final LinkedHashMap<String, Option> options = new LinkedHashMap<String, Option>();
    final LinkedHashMap<String, Message> messages = new LinkedHashMap<String, Message>();
    final LinkedHashMap<String, EnumGroup> enumGroups = new LinkedHashMap<String, EnumGroup>();
    
    public Proto()
    {
        this(null, DEFAULT_LOADER, null);
    }
    
    public Proto(File file)
    {
        this(file, DEFAULT_LOADER, null);
    }
    
    public Proto(Loader loader)
    {
        this(null, loader, null);
    }
    
    public Proto(File file, Loader loader)
    {
        this(file, loader, null);
    }
    
    public Proto(File file, Loader loader, Proto importer)
    {
        this.file = file;
        this.loader = loader;
        this.importer = importer;
    }
    
    public File getFile()
    {
        return file;
    }
    
    public String getPackageName()
    {
        return packageName;
    }
    
    public String getJavaPackageName()
    {
        return javaPackageName;
    }
    
    void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }
    
    public Option getOption(String name)
    {
        return options.get(name);
    }
    
    public Collection<Option> getOptions()
    {
        return options.values();
    }
    
    void addOption(Option option)
    {
        options.put(option.name, option);
    }
    
    public Collection<Message> getMessages()
    {
        return messages.values();
    }
    
    public Message getMessage(String name)
    {
        return messages.get(name);
    }
    
    void addMessage(Message message)
    {
        messages.put(message.name, message);
        message.proto = this;
    }
    
    public Collection<EnumGroup> getEnumGroups()
    {
        return enumGroups.values();
    }
    
    public EnumGroup getEnumGroup(String name)
    {
        return enumGroups.get(name);
    }
    
    void addEnumGroup(EnumGroup enumGroup)
    {
        enumGroups.put(enumGroup.name, enumGroup);
        enumGroup.proto = this;
    }
    
    public Collection<Proto> getImportedProtos()
    {
        return importedProtos.values();
    }
    
    public Proto getImportedProto(String packageName)
    {
        return importedProtos.get(packageName);
    }
    
    void importProto(String path)
    {
        try
        {
            addImportedProto(loader.load(path, this));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    void addImportedProto(Proto proto)
    {
        importedProtos.put(proto.packageName, proto);
    }
    
    void postParse()
    {
        Option pkgOption = options.get("java_package");
        javaPackageName = pkgOption==null || pkgOption.value.length()==0 ? packageName : 
            pkgOption.value;
        
        for(Message m : getMessages())
            m.resolveReferences();
    }
    
    public String toString()
    {
        return new StringBuilder()
            .append('{')
            .append("packageName:").append(packageName)
            .append(',').append("options:").append(getOptions())
            .append(',').append("messages:").append(getMessages())
            .append('}')
            .toString();
    }
    
    public static class Option
    {
        final String name;
        final String value;
        final boolean standard;

        public Option(String name, String value, boolean standard)
        {
            this.name = name;
            this.value = value;
            this.standard = standard;
        }

        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * @return the value
         */
        public String getValue()
        {
            return value;
        }

        /**
         * @return the isStandard
         */
        public boolean isStandard()
        {
            return standard;
        }
        
        public String toString()
        {
            return new StringBuilder()
                .append('{')
                .append(name).append(':').append(value)
                .append(',').append("standard:").append(standard)
                .append('}')
                .toString();
        }
        
    }
    
    public interface Loader
    {
        public Proto load(String path, Proto importer) throws Exception;
    }
    
    public static final Loader DEFAULT_LOADER = new Loader()
    {

        public Proto load(String path, Proto importer) throws Exception
        {
            File importerFile = importer.getFile();
            File protoFile;
            if(importerFile==null)
            {
                protoFile = new File(path);
                if(!protoFile.exists())
                    throw new IllegalStateException("Imported proto " + path + " not found.");
            }
            else
            {
                protoFile = new File(importerFile.getParentFile(), path);
                if(!protoFile.exists() && !(protoFile=new File(path)).exists())
                    throw new IllegalStateException("Imported proto " + path + " not found.");
            }
            
            Proto proto = new Proto(protoFile, this, importer);
            ProtoUtil.loadFrom(protoFile, proto);
            return proto;
        }
        
    };

}
