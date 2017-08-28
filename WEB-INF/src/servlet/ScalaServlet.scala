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
        val body = Model(Vector("Name" -> "Test Server", "Time" -> Instant.now(), "Language" -> "Scala"))
        Response(body).update(response)
    }
}