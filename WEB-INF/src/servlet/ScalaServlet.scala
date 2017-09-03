package servlet

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ValueConversions._
import java.time.Instant
import http.Response
import utopia.flow.generic.DataType
import utopia.flow.generic.StringType
import collection.JavaConverters._
import util.NullSafe._
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import javax.servlet.annotation.MultipartConfig

/**
 * This servlet implementation uses scala instead of java
 * @author Mikko Hilpinen
 * @since 21.8.2017
 */
@MultipartConfig
class ScalaServlet extends HttpServlet
{
    // IMPLEMENTED METHODS    ----------------
    
    override def doGet(request: HttpServletRequest, response: HttpServletResponse) = 
    {
        DataType.setup()
        
        val method = request.getMethod.toOption
        val uri = request.getRequestURI.toOption
        val url = request.getRequestURL.toString()
        val cType = request.getContentType.toOption
        val parameterNames = request.getParameterNames.asScala.toVector
        val headerNames = request.getHeaderNames.asScala.toVector
        
        val parameterStr = parameterNames.foldLeft("") { (str, pName) => 
                str + s", $pName = ${ request.getParameter(pName) }" }
        val headerStr = headerNames.reduceOption { (str, hName) => 
                str + s", $hName = ${ request.getHeader(hName) }" }
        
        val parts = Try(request.getParts).map { _.asScala.map { part => 
                s"${ part.getName } (${ part.getContentType })" } }
        
        val partString = parts match 
        {
            case Success(partIterable) => partIterable.foldLeft("") { _ + ", " + _ }
            case Failure(t) => t.getMessage
        }
        //if (parts.isSuccess) parts.get.foldLeft("") { _ + ", " + _ } else ""
        
        val body = Model(Vector("Method" -> method, "uri" -> uri, "url" -> url, "Content-Type" -> cType, 
                "Parameters" -> parameterStr, "Headers" -> headerStr, "Parts" -> partString))
        Response.fromModel(body).update(response)
    }
    
    override def doPost(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
    override def doPut(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
    override def doDelete(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
    override def doHead(request: HttpServletRequest, response: HttpServletResponse) = doGet(request, response)
}