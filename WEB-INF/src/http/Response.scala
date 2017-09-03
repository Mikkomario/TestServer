package http

import javax.servlet.http.HttpServletResponse
import java.io.PrintWriter
import utopia.flow.datastructure.template.Model
import utopia.flow.datastructure.template.Property
import java.io.OutputStream
import java.nio.file
import java.nio.file.Files
import utopia.flow.datastructure.immutable
import http.ContentCategory.Application

object Response
{
    // OTHER METHODS    ----------------
    
    /**
     * Wraps a model body into a JSON response
     * @param body the model that forms the body of the response
     * @param status the status of the response
     */
    def fromModel(body: Model[Property], status: Status = OK, setCookies: Seq[Cookie] = Vector()) = 
            new Response(status, Headers().withContentType(Application/"json"), setCookies, 
                    Some(writeString(body.toJSON, _)))
    
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
            new Response(status, headers, setCookies, Some(Files.copy(filePath, _)))
        }
        else
        {
            new Response(NotFound)
        }
    }
    
    /**
     * Writes a string to the output stream. Can be used in the writeBody function
     */
    def writeString(string: String, stream: OutputStream) = 
    {
        val writer = new PrintWriter(stream)
        try
        {
            writer.write(string)
        }
        finally
        {
            writer.close
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
case class Response(val status: Status = OK, val headers: Headers = Headers(), 
        val setCookies: Seq[Cookie] = Vector(), writeBody: Option[OutputStream => Unit] = None)
{
    // OPERATORS    --------------------
    
    /**
     * Creates a new response with a cookie added to it
     */
    def +(cookie: Cookie) = copy(setCookies = setCookies :+ cookie)
    
    
    // OTHER METHODS    ----------------
    
    /**
     * Creates a new response with modified headers. The headers are modified in the provided 
     * function
     */
    def withModifiedHeaders(modify: Headers => Headers) = copy(headers = modify(headers))
    
    /**
     * Updates the contents of a servlet response to match those of this response
     */
    // TODO: You probably want to move this elsewhere to avoid dependencies
    def update(response: HttpServletResponse) = 
    {
        response.setStatus(status.code)
        headers.contentType.foreach { cType => response.setContentType(cType.toString) }
        
        headers.fields.foreach { case (headerName, value) => response.addHeader(headerName, value) }
        
        if (writeBody.isDefined)
        {
            val stream = response.getOutputStream
            try
            {
                writeBody.get(stream)
            }
            finally
            {
                try { stream.flush() } finally { stream.close() }
            }
        }
    }
}