package com.ibm.ws.jsf22.fat.PI67525;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.servlet.http.Part;

/**
 *
 */
@Named
@RequestScoped
public class UploadBean {

    Part uploadFile;

    public Part getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(Part uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String execute() {
        System.out.println("Upload FileName: " + uploadFile.getSubmittedFileName());
        return "index?faces-redirect=true";
    }

}
