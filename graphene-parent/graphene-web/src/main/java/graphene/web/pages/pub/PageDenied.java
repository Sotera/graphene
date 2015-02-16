package graphene.web.pages.infrastructure;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;


/**
 * Intended for use with PageProtectionFilter, this displays the path of the page to which you are not authorised.
 */
public class PageDenied {

    // Activation context

    @Property
    private String urlDenied;

    // Other useful bits and pieces

    @Inject
    private Response response;

    // The code

    String onPassivate() {
        return urlDenied;
    }

    void onActivate(String urlDenied) {
        this.urlDenied = urlDenied;
    }

    public void setupRender() {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

}