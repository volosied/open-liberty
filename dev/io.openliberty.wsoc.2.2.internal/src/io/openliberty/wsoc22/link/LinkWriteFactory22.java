package io.openliberty.wsoc22.link;

import com.ibm.ws.wsoc.link.LinkWrite;
import com.ibm.ws.wsoc.link.LinkWriteFactory;

public class LinkWriteFactory22 implements LinkWriteFactory {
    
    public LinkWrite getLinkWrite() {
        System.out.println("USING 22 BUNDLE");
        return new LinkWriteExt22();
    }
}
