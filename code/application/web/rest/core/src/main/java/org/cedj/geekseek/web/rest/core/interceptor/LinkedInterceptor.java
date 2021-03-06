package org.cedj.geekseek.web.rest.core.interceptor;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;

import org.cedj.geekseek.web.rest.core.LinkProvider;
import org.cedj.geekseek.web.rest.core.LinkableRepresenatation;

public class LinkedInterceptor implements RESTInterceptor {

    @Inject
    private Instance<LinkProvider> linkProviers;

    @Override
    public int getPriority() {
        return -10;
    }

    @Override
    public Object invoke(InvocationContext ic) throws Exception {
        Object obj = ic.proceed();
        if(hasLinkableRepresentations(obj)) {
            linkAllRepresentations(obj);
        }
        return obj;
    }

    private boolean hasLinkableRepresentations(Object obj) {
        return locateLinkableRepresenatation(obj) != null;
    }

    private LinkableRepresenatation<?> locateLinkableRepresenatation(Object obj) {
        if(obj instanceof Response) {
            Object entity = ((Response)obj).getEntity();
            if(entity instanceof LinkableRepresenatation) {
                return (LinkableRepresenatation<?>)entity;
            }
        }
        return null;
    }

    private void linkAllRepresentations(Object obj) {
        LinkableRepresenatation<?> linkable = locateLinkableRepresenatation(obj);
        for(LinkProvider linker : linkProviers) {
            linker.appendLinks(linkable);
        }
    }
}
