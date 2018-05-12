package http

import utopia.access.http.ContentCategory._

import java.io.BufferedReader
import utopia.access.http.ContentType
import utopia.access.http.Headers

/**
* This class represents a body send along with a request
* @author Mikko Hilpinen
* @since 12.5.2018
**/
class StreamedBody(val reader: BufferedReader, val contentType: ContentType = Text/"plain", 
        val contentLength: Option[Long] = None, val headers: Headers = Headers())
{
    
}