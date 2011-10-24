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

package com.dyuproject.protostuff.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Represents the message defined in the {@link Proto}.
 *
 * @author David Yu
 * @created Dec 19, 2009
 */
public class Message extends AnnotationContainer implements HasName, HasFields
{
    
    String name;
    Proto proto;
    Message parentMessage;
    final LinkedHashMap<String, Message> nestedMessages = new LinkedHashMap<String, Message>();
    final LinkedHashMap<String,EnumGroup> nestedEnumGroups = new LinkedHashMap<String,EnumGroup>();
    final LinkedHashMap<String,Field<?>> fields = new LinkedHashMap<String,Field<?>>();
    final ArrayList<Extension> nestedExtensions = new ArrayList<Extension>();
    final ArrayList<Field<?>> sortedFields = new ArrayList<Field<?>>();
    // code generator helpers
    boolean bytesFieldPresent, repeatedFieldPresent, requiredFieldPresent, extensible;
    boolean requiredFieldPresentOnCurrent;
    
    final ArrayList<int[]> extensionRanges = new ArrayList<int[]>();
    final LinkedHashMap<Integer, Field<?>> extensions = new LinkedHashMap<Integer,Field<?>>();
    
    public Message()
    {
        
    }
    
    public Message(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Proto getProto()
    {
        Proto p = proto;
        if(p==null)
            proto = p = parentMessage.getProto();
        return p;
    }
    
    public Message getRootMessage()
    {
        return parentMessage==null ? null : getRoot(parentMessage);
    }
    
    public Message getParentMessage()
    {
        return parentMessage;
    }
    
    public boolean isNested()
    {
        return parentMessage!=null;
    }
    
    public boolean hasNestedMessages()
    {
        return !nestedMessages.isEmpty();
    }
    
    public boolean hasNestedEnumGroups()
    {
        return !nestedEnumGroups.isEmpty();
    }
    
    public Collection<Message> getNestedMessages()
    {
        return nestedMessages.values();
    }
    
    public Message getNestedMessage(String name)
    {
        return nestedMessages.get(name);     
    }
    
    void addNestedMessage(Message message)
    {
        nestedMessages.put(message.name, message);
        message.parentMessage = this;
    }
    
    public Collection<EnumGroup> getNestedEnumGroups()
    {
        return nestedEnumGroups.values();
    }
    
    public EnumGroup getNestedEnumGroup(String name)
    {
        return nestedEnumGroups.get(name);
    }
    
    void addNestedEnumGroup(EnumGroup enumGroup)
    {
        nestedEnumGroups.put(enumGroup.name, enumGroup);
        enumGroup.parentMessage = this;
    }
    
    public Collection<Field<?>> getFields()
    {
        return sortedFields;
    }
    
    public Field<?> getField(String name)
    {
        return fields.get(name);
    }
    
    public boolean isDescendant(Message other)
    {
        if(parentMessage==null)
            return false;
        return parentMessage == other || parentMessage.isDescendant(other);
    }
    
    public Message getDescendant(String name)
    {
        if(parentMessage==null)
            return null;
        
        return name.equals(parentMessage.name) ? parentMessage : parentMessage.getDescendant(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Field<?>> T getField(String name, Class<T> typeClass)
    {
        return (T)fields.get(name);
    }
    
    public void addField(Field<?> field)
    {
        if(field.number<1)
        {
            throw new IllegalArgumentException("Invalid field number " + field.number 
                    + " from field " + field.name);
        }
        fields.put(field.name, field);
    }
    
    public void defineExtensionRange(int first, int last)
    {
        extensionRanges.add(new int[]{ first, last });
        this.extensible = true;
    }

    public void addNestedExtension(Extension extension) 
    {
        this.nestedExtensions.add(extension);
    }

    public Collection<Extension> getNestedExtensions()
    {
        return this.nestedExtensions;
    }
    
    public void extend(Extension extension)
    {
        if (isExtensible() == false)
        {
            throw new IllegalStateException("Message " + getFullName()
                    + " does not define extension range.");
        }

        for (Field<?> field : extension.getFields())
        {
            int number = field.getNumber();
            boolean inRange = false;
            for (int[] range : extensionRanges)
            {
                if (number >= range[0] && number <= range[1])
                {
                    inRange = true;
                    break;
                }
            }
            if (inRange == false)
            {
                throw new IllegalStateException("Extension '" + field.getName()
                        + "' is outside extension range.");
            }
            if (this.extensions.containsKey(number))
            {
                throw new IllegalStateException("Extension already defined for number '" + number
                        + "'.");
            }
            this.extensions.put(number, field);
        }
    }
    
    public String toString()
    {
        return new StringBuilder()
            .append('{')
            .append("name:").append(name)
            .append(',').append("enumGroups:").append(nestedEnumGroups.values())
            .append(',').append("extensions:").append(nestedExtensions)
            .append(',').append("fields:").append(fields.values())
            .append('}')
            .toString();
    }
    
    public String getFullName()
    {
        StringBuilder buffer = new StringBuilder();
        resolveFullName(this, buffer);
        return buffer.toString();
    }
    
    public String getJavaFullName()
    {
        StringBuilder buffer = new StringBuilder();
        resolveJavaFullName(this, buffer);
        return buffer.toString();
    }
    
    public String getRelativeName()
    {
        StringBuilder buffer = new StringBuilder();
        resolveRelativeName(this, buffer, null);
        return buffer.toString();
    }
    
    public boolean isRepeatedFieldPresent()
    {
        return repeatedFieldPresent;
    }
    
    public boolean isBytesFieldPresent()
    {
        return bytesFieldPresent;
    }
    
    public boolean isRequiredFieldPresent()
    {
        return requiredFieldPresent;
    }
    
    public boolean isRequiredFieldPresentOnCurrent()
    {
        return requiredFieldPresentOnCurrent;
    }
    
    public boolean isExtensible() 
    {
        return extensible;
    }
    
    // post parse
    
    void resolveReferences(Message root)
    {
        getProto();
        for(Field<?> f : fields.values())
        {
            f.owner = this;
            if(!root.repeatedFieldPresent && f.isRepeated())
                root.repeatedFieldPresent = true;
            
            if(!root.requiredFieldPresent && f.isRequired())
                root.requiredFieldPresent = true;
            
            if(!requiredFieldPresentOnCurrent && f.isRequired())
                requiredFieldPresentOnCurrent = true;
            
            if(f instanceof Field.Bytes)
            {
                if(!root.bytesFieldPresent)
                    root.bytesFieldPresent = true;
            }
            else if(f instanceof Field.String)
            {    
                if(!root.bytesFieldPresent && f.defaultValue!=null)
                    root.bytesFieldPresent = true;
            }
            else if(f instanceof Field.Reference)
            {
                Field.Reference fr = (Field.Reference)f;
                String refName = fr.refName;
                String packageName = fr.packageName;
                String fullRefName = (packageName == null ? refName : packageName + '.' + refName);

                HasName refObj = proto.findReference(fullRefName, getFullName());
                if (refObj instanceof Message)
                {
                    MessageField mf = newMessageField((Message) refObj, fr, this);
                    fields.put(mf.name, mf);
                    continue;
                }
                
                if (refObj instanceof EnumGroup)
                {
                    EnumField ef = newEnumField((EnumGroup) refObj, fr, this);
                    fields.put(ef.name, ef);
                    continue;
                }
                
                throw new IllegalStateException("unknown field: " + fullRefName);
            }
        }
        sortedFields.addAll(fields.values());
        Collections.sort(sortedFields);
        
        for(Extension extension : this.nestedExtensions)
            extension.resolveReferences();
        
        for(Message m : nestedMessages.values())
            m.resolveReferences(root);
    }
    
    void cacheFullyQualifiedNames()
    {
        Proto proto = getProto();
        proto.fullyQualifiedMessages.put(getFullName(), this);
        
        for (Message m : nestedMessages.values())
            m.cacheFullyQualifiedNames();
        for (EnumGroup eg : nestedEnumGroups.values())
            eg.cacheFullyQualifiedName();
    }
    
    static MessageField newMessageField(Message message, Field.Reference fr, Message owner)
    {
        MessageField mf = new MessageField(message);
        mf.owner = owner;
        mf.packable = false;
        copy(fr, mf);
        //System.err.println(owner.getRelativeName() + "." + mf.name +": " + mf.getJavaType());
        return mf;
    }
    
    static EnumField newEnumField(EnumGroup enumGroup, Field.Reference fr, Message owner)
    {
        EnumField ef = new EnumField(enumGroup);
        ef.owner = owner;
        ef.packable = true;
        String refName = (String)fr.getDefaultValue();
        if(refName == null)
            ef.defaultValue = enumGroup.getFirstValue();
        else
        {
            ef.defaultValue = enumGroup.getValue(refName);
            if(ef.defaultValue == null)
            {
                throw new IllegalStateException("The field: " + ef.name + 
                        " contains an unknown enum value: " + refName);
            }
        }
        copy(fr, ef);
        //System.err.println(owner.getRelativeName() + "." + ef.name +": " + ef.getJavaType());
        return ef;
    }
    
    static void copy(Field<?> from, Field<?> to)
    {
        to.name = from.name;
        to.number = from.number;
        to.modifier = from.modifier;
        to.addAnnotations(from.annotations, true);
    }
    
    static void resolveFullName(Message message, StringBuilder buffer)
    {
        buffer.insert(0, message.name).insert(0, '.');
        if(message.isNested())
            resolveFullName(message.parentMessage, buffer);
        else
            buffer.insert(0, message.getProto().getPackageName());
    }
    
    static void resolveJavaFullName(Message message, StringBuilder buffer)
    {
        buffer.insert(0, message.name).insert(0, '.');
        if(message.isNested())
            resolveFullName(message.parentMessage, buffer);
        else
            buffer.insert(0, message.getProto().getJavaPackageName());
    }
    
    static void resolveRelativeName(Message message, StringBuilder buffer, Message descendant)
    {
        buffer.insert(0, message.name);
        if(message.parentMessage!=null)
        {
            if(message.parentMessage!=descendant)
            {
                buffer.insert(0, '.');
                resolveRelativeName(message.parentMessage, buffer, descendant);
            }
        }
    }
    
    static void computeName(Message message, Message owner, StringBuilder buffer)
    {
        if(owner==message || message.parentMessage==owner || owner.isDescendant(message))
            buffer.append(message.name);
        else if(message.isDescendant(owner))
            Message.resolveRelativeName(message, buffer, owner);
        else if(message.getProto().getJavaPackageName().equals(owner.getProto().getJavaPackageName()))
            buffer.append(message.getRelativeName());
        else
            buffer.append(message.getJavaFullName());
    }
    
    static Message getRoot(Message parent)
    {
        return parent.parentMessage==null ? parent: getRoot(parent.parentMessage);
    }

}