// Generated by http://code.google.com/p/protostuff/ ... DO NOT EDIT!
// Generated from protostuff-me.proto

package com.dyuproject.protostuff.me;

import java.io.IOException;
import java.util.Vector;

import com.dyuproject.protostuff.me.Input;
import com.dyuproject.protostuff.me.Message;
import com.dyuproject.protostuff.me.Output;
import com.dyuproject.protostuff.me.Pipe;
import com.dyuproject.protostuff.me.Schema;

public final class Club implements Message, Schema
{

    public static Schema getSchema()
    {
        return DEFAULT_INSTANCE;
    }

    public static Club getDefaultInstance()
    {
        return DEFAULT_INSTANCE;
    }

    static final Club DEFAULT_INSTANCE = new Club();

    
    private String name;
    private Vector student;
    private Vector partnerClub;

    public Club()
    {
        
    }

    // getters and setters

    // name

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    // student

    public Vector getStudentList()
    {
        return student;
    }

    public void setStudentList(Vector student)
    {
        this.student = student;
    }

    public Student getStudent(int index)
    {
        return student == null ? null : (Student)student.elementAt(index);
    }

    public int getStudentCount()
    {
        return student == null ? 0 : student.size();
    }

    public void addStudent(Student student)
    {
        if(this.student == null)
            this.student = new Vector();
        this.student.addElement(student);
    }

    // partnerClub

    public Vector getPartnerClubList()
    {
        return partnerClub;
    }

    public void setPartnerClubList(Vector partnerClub)
    {
        this.partnerClub = partnerClub;
    }

    public Club getPartnerClub(int index)
    {
        return partnerClub == null ? null : (Club)partnerClub.elementAt(index);
    }

    public int getPartnerClubCount()
    {
        return partnerClub == null ? 0 : partnerClub.size();
    }

    public void addPartnerClub(Club partnerClub)
    {
        if(this.partnerClub == null)
            this.partnerClub = new Vector();
        this.partnerClub.addElement(partnerClub);
    }

    // message method

    public Schema cachedSchema()
    {
        return DEFAULT_INSTANCE;
    }

    // schema methods

    public Object /*Club*/ newMessage()
    {
        return new Club();
    }

    public Class typeClass()
    {
        return Club.class;
    }

    public String messageName()
    {
        return "Club";
    }

    public String messageFullName()
    {
        return Club.class.getName();
    }

    public boolean isInitialized(Object /*Club*/ message)
    {
        return true;
    }

    public void mergeFrom(Input input, Object /*Club*/ messageObj) throws IOException
    {
        Club message = (Club)messageObj;
        for(int number = input.readFieldNumber(this);; number = input.readFieldNumber(this))
        {
            switch(number)
            {
                case 0:
                    return;
                case 1:
                    message.name = input.readString();
                    break;

                case 2:
                    if(message.student == null)
                        message.student = new Vector();
                    message.student.addElement(input.mergeObject(null, Student.getSchema()));
                    break;

                case 3:
                    if(message.partnerClub == null)
                        message.partnerClub = new Vector();
                    message.partnerClub.addElement(input.mergeObject(null, Club.getSchema()));
                    break;

                default:
                    input.handleUnknownField(number, this);
            }   
        }
    }

    public void mergeFrom(Object /*Club*/ messageObj)
    {
        Club message = (Club)messageObj;
        this.name = message.name;
        this.student = new Vector();
        if(message.student != null) {
            for(int i = 0; i < message.student.size(); i++) {
                Student origElt = (Student)message.student.elementAt(i);
                Student newElt = (Student)origElt.newMessage();
                newElt.mergeFrom((Student)message.student.elementAt(i));
                this.student.addElement(newElt);
            }
        }
        this.partnerClub = new Vector();
        if(message.partnerClub != null) {
            for(int i = 0; i < message.partnerClub.size(); i++) {
                Club origElt = (Club)message.partnerClub.elementAt(i);
                Club newElt = (Club)origElt.newMessage();
                newElt.mergeFrom((Club)message.partnerClub.elementAt(i));
                this.partnerClub.addElement(newElt);
            }
        }
    }


    public void writeTo(Output output, Object /*Club*/ messageObj) throws IOException
    {
        Club message = (Club)messageObj;
        if(message.name != null)
            output.writeString(1, message.name, false);


        if(message.student != null)
        {
            for(int i = 0; i < message.student.size(); i++)
            {
                Student student = (Student)message.student.elementAt(i);
                if(student != null)
                    output.writeObject(2, student, Student.getSchema(), true);
            }
        }


        if(message.partnerClub != null)
        {
            for(int i = 0; i < message.partnerClub.size(); i++)
            {
                Club partnerClub = (Club)message.partnerClub.elementAt(i);
                if(partnerClub != null)
                    output.writeObject(3, partnerClub, Club.getSchema(), true);
            }
        }

    }

    public String getFieldName(int number)
    {
        switch(number)
        {
            case 1: return "name";
            case 2: return "student";
            case 3: return "partnerClub";
            default: return null;
        }
    }

    public int getFieldNumber(String name)
    {
        final Integer number = (Integer)__fieldMap.get(name);
        return number == null ? 0 : number.intValue();
    }

    private static final java.util.Hashtable __fieldMap = new java.util.Hashtable();
    static
    {
        __fieldMap.put("name", new Integer(1));
        __fieldMap.put("student", new Integer(2));
        __fieldMap.put("partnerClub", new Integer(3));
    }
    
    static final Pipe.Schema PIPE_SCHEMA = new Pipe.Schema(DEFAULT_INSTANCE)
    {
        protected void transfer(Pipe pipe, Input input, Output output) throws IOException
        {
            for(int number = input.readFieldNumber(wrappedSchema);; number = input.readFieldNumber(wrappedSchema))
            {
                switch(number)
                {
                    case 0:
                        return;
                    case 1:
                        input.transferByteRangeTo(output, true, number, false);
                        break;

                    case 2:
                        output.writeObject(number, pipe, Student.getPipeSchema(), true);
                        break;

                    case 3:
                        output.writeObject(number, pipe, Club.getPipeSchema(), true);
                        break;

                    default:
                        input.handleUnknownField(number, wrappedSchema);
                }
            }
        }
    };

    public static Pipe.Schema getPipeSchema()
    {
        return PIPE_SCHEMA;
    }

}
