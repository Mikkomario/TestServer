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
        def toRequest = 
        {
            val method = r.getMethod.toOption.flatMap(Method.parse)
            val path = r.getRequestURI.toOption.map(Path.parse)
            
            // TODO: Add parameter decoding
            // TODO: Also, should parameter values be parsed form json?
            // val parameters = Model(r.getParameterNames.asScala.map {  })
        }
    }
}