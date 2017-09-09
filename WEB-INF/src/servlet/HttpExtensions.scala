package servlet

import utopia.flow.util.NullSafe._
import collection.JavaConverters._
import javax.servlet.http.HttpServletResponse
import http.Response
import javax.servlet.http.HttpServletRequest
import http.Cookie
import http.Method
import http.Path
import utopia.flow.datastructure.immutable.Model
import jdk.nashorn.internal.parser.JSONParser
import utopia.flow.parse.JSONReader
import http.Headers
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.StringType
import scala.util.Try
import http.ContentType
import http.FileUpload
import http.Request

/**
 * This object contains extensions that can be used with HttpServletRequest and HttpServletResponse 
 * classes on server side.
 * @author Mikko Hilpinen
 * @since 9.9.2017
 */
object HttpExtensions
{
    implicit class TomcatResponse(val r: Response) extends AnyVal
    {
        /**
         * Updates the contents of a servlet response to match those of this response
         */
        def update(response: HttpServletResponse) = 
        {
            response.setStatus(r.status.code)
            r.headers.contentType.foreach { cType => response.setContentType(cType.toString) }
            
            r.headers.fields.foreach { case (headerName, value) => response.addHeader(headerName, value) }
            
            r.setCookies.foreach( cookie => 
            {
                val javaCookie = new javax.servlet.http.Cookie(cookie.name, cookie.value.toJSON)
                cookie.lifeLimitSeconds.foreach { javaCookie.setMaxAge(_) }
                javaCookie.setSecure(cookie.isSecure)
                
                response.addCookie(javaCookie)
            })
            
            if (r.writeBody.isDefined)
            {
                val stream = response.getOutputStream
                try
                {
                    r.writeBody.get(stream)
                }
                finally
                {
                    try { stream.flush() } finally { stream.close() }
                }
            }
        }
    }
    
    implicit class ConvertibleRequest(val r: HttpServletRequest) extends AnyVal
    {
        def toRequest(fileUploadPath: java.nio.file.Path) = 
        {
            val method = r.getMethod.toOption.flatMap(Method.parse)
            
            if (method.isDefined)
            {
                val path = r.getRequestURI.toOption.flatMap(Path.parse)
                
                // TODO: Add parameter decoding
                val paramValues = r.getParameterNames.asScala.map { pName => 
                        (pName, JSONReader.parseValue(r.getParameter(pName))) }.flatMap { case (name, value) => 
                        if (value.isSuccess) Some(name, value.get) else None }
                val parameters = Model(paramValues.toVector)
                
                val headers = Headers(r.getHeaderNames.asScala.map { hName => (hName, r.getHeader(hName)) }.toMap)
                
                val cookies = r.getCookies.map { javaCookie => Cookie(javaCookie.getName, 
                        javaCookie.getValue.toOption.flatMap { 
                        JSONReader.parseValue(_).toOption }.getOrElse(Value.empty(StringType)), 
                        if (javaCookie.getMaxAge < 0) None else Some(javaCookie.getMaxAge), 
                        javaCookie.getSecure) }
                
                val uploads = Try(r.getParts).toOption.map { _.asScala.flatMap {part => 
                        part.getContentType.toOption.flatMap(ContentType.parse).map { 
                        new FileUpload(fileUploadPath, part.getName, part.getSize, _, 
                        part.getSubmittedFileName, part.getInputStream, part.write) }}}
                
                Some(new Request(method.get, path, parameters, headers, cookies, 
                        uploads.getOrElse(Vector())))
            }
            else
            {
                None
            }
        }
    }
}