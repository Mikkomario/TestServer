package servlet

import servlet.HttpExtensions._

import javax.servlet.http.HttpServlet
import javax.servlet.annotation.MultipartConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import utopia.flow.generic.DataType
import http.ServerSettings
import java.nio.file.Paths
import rest.RequestHandler
import http.Path
import rest.FilesResource
import http.Response
import http.BadRequest
import test.TestRestResource

/**
 * This servlet offers a test implementation of a restful interface using the classes in the project
 * @author Mikko Hilpinen
 * @since 24.9.2017
 */
@MultipartConfig(
        fileSizeThreshold   = 1048576,  // 1 MB
        maxFileSize         = 10485760, // 10 MB
        maxRequestSize      = 20971520, // 20 MB
        location            = "D:/Uploads"
)
class RestServlet extends HttpServlet
{
    // INITIAL CODE    -------------------------
    
    DataType.setup()
    private implicit val settings = ServerSettings("http://localhost:9999", Paths.get("D:/Uploads"))
    private val handler = new RequestHandler(Vector(new FilesResource("files"), 
            new TestRestResource("test")), Some(Path("TestServer", "rest")))
    
    
    // IMPLEMENTED METHODS    ------------------
    
    override def doGet(request: HttpServletRequest, response: HttpServletResponse) = 
    {
        val parsedResponse = request.toRequest.map(handler.apply).getOrElse(
            Response.plainText("Couldn't parse the request", BadRequest));
        
        parsedResponse.update(response)
    }
    
    override def doPost(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
    override def doPut(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
    override def doDelete(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
    override def doHead(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
}