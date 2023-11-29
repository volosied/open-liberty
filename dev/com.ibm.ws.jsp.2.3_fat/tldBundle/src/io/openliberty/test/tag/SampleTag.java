package io.openliberty.test.tag;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;


public class SampleTag implements Tag {
  private PageContext pageContext;
  
  private Tag parent;
  
  public void setPageContext(PageContext pageContext) {
    this.pageContext = pageContext;
  }
  
  public void setParent(Tag parent) {
    this.parent = parent;
  }
  
  public Tag getParent() {
    return this.parent;
  }
  
  public int doStartTag() throws JspTagException {
    return 1;
  }
  
  public int doEndTag() throws JspTagException {  
        return 0;
  }
  
  public void release() {}
}