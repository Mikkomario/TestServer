package servlet

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import utopia.flow.generic.DataType
import utopia.flow.datastructure.immutable.Model
import java.time.Instant
import utopia.flow.generic.ValueConversions._
import utopia.nexus.http.Response
import java.nio.file.Paths
import java.nio.file.Files
import utopia.nexus.servlet.HttpExtensions._
import utopia.access.http.NotFound

/**
 * This servlet implementation uses scala instead of java
 * @author Mikko Hilpinen
 * @since 21.8.2017
 */
class AnkkaServlet extends HttpServlet
{
    // IMPLEMENTED METHODS    ----------------
    
    override def doGet(request: HttpServletRequest, response: HttpServletResponse) = 
    {
        DataType.setup()
        
        val filePath = Paths.get("webapps", "TestServer", "images", "ankka.jpg")
        
        if (Files.exists(filePath))
        {
            Response.fromFile(filePath).withModifiedHeaders { _.withCurrentDate }.update(response)
        }
        else
        {
            val body = Model(Vector("Server Path" -> Paths.get(".").toAbsolutePath().toString()))
            Response.fromModel(body, NotFound).update(response)
        }
    }
}