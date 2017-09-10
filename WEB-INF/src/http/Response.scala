package http

import java.io.PrintWriter
import utopia.flow.datastructure.template.Model
import utopia.flow.datastructure.template.Property
import java.io.OutputStream
import java.nio.file
import java.nio.file.Files
import utopia.flow.datastructure.immutable
import http.ContentCategory.Application
import java.nio.charset.StandardCharsets

object Response
{
    // OTHER METHODS    ----------------
    
    /**
     * Wraps a model body into an UTF-8 encoded JSON response
     * @param body the model that forms the body of the response
     * @param status the status of the response
     */
    def fromModel(body: Model[Property], status: Status = OK, setCookies: Seq[Cookie] = Vector()) = 
            new Response(status, Headers().withCurrentDate.withContentType(Application/"json"), setCookies, 
                    Some(_.write(body.toJSON.getBytes(StandardCharsets.UTF_8))))
    
    /**
     * Wraps a file into a response
     * @param filePath A path leading to the target file
     * @param contentType The content type associated with the file. If None (default), the program will 
     * attempt to guess the content type based on the file name.
     * @param status The status for the response. Default OK (200)
     */
    def fromFile(filePath: file.Path, contentType: Option[ContentType] = None, status: Status = OK, 
            setCookies: Seq[Cookie] = Vector()) = 
    {
        if (Files.exists(filePath) && !Files.isDirectory(filePath))
        {
            val contentType = ContentType.guessFrom(filePath.getFileName.toString())
            val headers = if (contentType.isDefined) Headers().withContentType(contentType.get) else Headers()
            new Response(status, headers.withCurrentDate, setCookies, Some(Files.copy(filePath, _)))
        }
        else
        {
            new Response(NotFound)
        }
    }
}

/**
 * Responses are used for returning data from server side to client side
 * @author Mikko Hilpinen
 * @since 20.8.2017
 * @param status the html status associated with this response. OK by default.
 * @param contentType the content type of this response. None by default.
 * @param writeBody a function that writes the response body into a stream. None by default.
 */
class Response(val status: Status = OK, val headers: Headers = Headers(), 
        val setCookies: Seq[Cookie] = Vector(), val writeBody: Option[OutputStream => Unit] = None)
{
    // OPERATORS    --------------------
    
    /**
     * Creates a new response with a cookie added to it
     */
    def +(cookie: Cookie) = new Response(status, headers, setCookies :+ cookie, writeBody)
    
    
    // OTHER METHODS    ----------------
    
    /**
     * Creates a new response with modified headers. The headers are modified in the provided 
     * function
     */
    def withModifiedHeaders(modify: Headers => Headers) = new Response(status, modify(headers), 
            setCookies, writeBody)
}