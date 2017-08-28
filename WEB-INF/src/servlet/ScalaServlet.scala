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

/**
 * This servlet implementation uses scala instead of java
 * @author Mikko Hilpinen
 * @since 21.8.2017
 */
class ScalaServlet extends HttpServlet
{
    // IMPLEMENTED METHODS    ----------------
    
    override def doGet(request: HttpServletRequest, response: HttpServletResponse) = 
    {
        DataType.setup()
        
        val method = request.getMethod.toOption
        val uri = request.getRequestURI.toOption
        val cType = request.getContentType.toOption
        val parameterNames = request.getParameterNames.asScala.toVector
        val headerNames = request.getHeaderNames.asScala.toVector
        
        val parameterStr = parameterNames.reduceOption { (str, pName) => 
                str + s", $pName = ${ request.getParameter(pName) }" }
        val headerStr = headerNames.reduceOption { (str, hName) => 
                str + s", $hName = ${ request.getHeader(hName) }" }
        
        val body = Model(Vector("Method" -> method, "uri" -> uri, "Content-Type" -> cType, 
                "Parameters" -> parameterStr, "Headers" -> headerStr))
        Response(body).update(response)
    }
}