package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet simply returns a hello html response
 * @author Mikko Hilpinen
 * @since 20.8.2017
 */
public class HelloServlet extends HttpServlet
{
	// ATTRIBUTES	-----------------
	
	private static final long serialVersionUID = 4023442997406361930L;
	
	
	// IMPLEMENTED METHODS	---------
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		//Model<Constant> model = new Model<Constant>();
		
		response.setContentType("text/html");
		
		try (PrintWriter out = response.getWriter())
		{
			out.println("<html>");
	        out.println("<head><title>Hello, World</title></head>");
	        out.println("<body>");
	        out.println("<h1>Hello, world!</h1>");  // says Hello
	        // Echo client's request information
	        out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
	        out.println("<p>Protocol: " + request.getProtocol() + "</p>");
	        out.println("<p>PathInfo: " + request.getPathInfo() + "</p>");
	        out.println("<p>Remote Address: " + request.getRemoteAddr() + "</p>");
	        // Generate a random number upon each request
	        out.println("<p>A Random Number: <strong>" + Math.random() + "</strong></p>");
	        out.println("</body></html>");
		}
	}
}
