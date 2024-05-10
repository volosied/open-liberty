package io.openliberty.wsoc21.link;

import com.ibm.ws.wsoc.link.LinkWrite;
import com.ibm.ws.wsoc.link.LinkWriteFactory;

public class LinkWriteFactory22 implements LinkWriteFactory {
    
    public LinkWrite getLinkWrite() {
        return new LinkWriteExt22();
    }
}
