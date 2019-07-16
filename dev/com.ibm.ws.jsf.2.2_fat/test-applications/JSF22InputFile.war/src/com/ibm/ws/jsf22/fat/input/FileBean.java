/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jsf22.fat.input;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.Part;

@ManagedBean
@SessionScoped
public class FileBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Part file;
    private String fileContents;

    public FileBean() {}

    public String uploadFile() {
        try {
            System.out.println("In uploadFile");
            if (file != null) {
                System.out.println("File to Upload is: " + file.toString());
                fileContents = new Scanner(file.getInputStream()).useDelimiter("\\A").next();
            } else {
                System.out.println("File is NULL");
            }

            System.out.println("fileContents: " + fileContents);
            return "fileUploadDisplay";
        } catch (IOException e) {
            System.out.println("Exception e: " + e);
        }
        return null;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public String getFileContents() {
        return fileContents;
    }
}