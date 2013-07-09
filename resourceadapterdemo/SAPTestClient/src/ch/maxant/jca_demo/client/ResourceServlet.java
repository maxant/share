package ch.maxant.jca_demo.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.resource.ResourceException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ResourceServlet")
public class ResourceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @EJB private SomeServiceThatCallsSAP svc;
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String name = request.getParameter("name");
        if(name == null) name = "name";
        try {
            String s = svc.doSomethingInvolvingSeveralResources(name);
            log.log(Level.INFO, "servlet got: " + s);
            response.getWriter().append(s);
        } catch (ResourceException | SQLException e) {
            throw new ServletException(e);
        }
	}

}
