package com.ibm.ws.jsp.translator.visitor.generator;

import java.util.ArrayList;

public class OutWriterList {
    
    public enum Type {
        PRINT,
        WRITE
    };


    public int getSize(){
        return list.size();
    }

    private class Group {
        String s;
        Type t;

        public String getString() {
            return s;
        }

        public String getType() {
            return t.toString();
        }

        Group(String s, Type t){
            this.s = s;
            this.t = t;
        }

        
    }

    public 

    ArrayList<Group> list = new ArrayList<Group>(); 

    public void queueMessages(String s, Type t) {
        System.out.println("Added to queue " + s + " " + t);
        list.add(new Group(s,t));
    }

    public String getNextJavaCode() throws Exception{

        String methodCallCode = null;

        System.out.println("list size of q: " + list.size());

        ArrayList<String> calls = new ArrayList<String>();

        while(list.size() != 0 && list.size() >= 2){
            Group g0 = list.get(0);
            Group g1 = list.get(1);
            System.out.println(g0.getType()+g1.getType());

            switch(g0.getType()+g1.getType()){
                case "PRINTPRINT":
                methodCallCode = String.format("_jsp_print(out, %s, %s);", g0.getString(), g1.getString());
                    break;
                case "PRINTWRITE":
                methodCallCode = String.format("_jsp_print_write(out, %s, %s);", g0.getString(), g1.getString());
                    break;
                case "WRITEPRINT":
                methodCallCode = String.format("_jsp_write_print_out(out, %s, %s);", g0.getString(), g1.getString());
                    break;
                case "WRITEWRITE":
                methodCallCode = String.format("_jsp_write(out, %s,  %s);", g0.getString(), g1.getString());

            }
            calls.add(methodCallCode);
            list.remove(0);
            list.remove(0);
        }

        if(list.size() == 1){
            Group g0 = list.get(0);
            switch(g0.getType()){
                case "PRINT":
                methodCallCode = String.format("out.print(%s);", g0.getString());
                    break;
                case "WRITE":
                methodCallCode = String.format("out.write(%s);", g0.getString());
            }
            calls.add(methodCallCode);
            list.remove(0);
        }

        System.out.println("RETURNING " +String.join("\n ", calls));
        return String.join("\n ", calls);
    }
    
}
