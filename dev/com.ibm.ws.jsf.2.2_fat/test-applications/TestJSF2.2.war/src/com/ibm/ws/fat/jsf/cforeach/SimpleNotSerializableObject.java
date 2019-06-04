package com.ibm.ws.fat.jsf.cforeach;

/**
 * Class used to test the c:forEach tag when object is not serializable
 */
public class SimpleNotSerializableObject {

    private Long id;
    private String value;

    public SimpleNotSerializableObject() {
        id = new Long(0L);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
