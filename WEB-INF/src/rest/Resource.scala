package rest

import utopia.flow.datastructure.template.Model
import utopia.flow.datastructure.template.Property
import http.Cookie
import http.FileUpload
import http.Headers
import http.Method
import http.Path
import http.Response
import http.Request
import http.ServerSettings

trait Resource
{
    // ABSTRACT PROPERTIES & METHODS ------------------
    
    /**
     * The name of this resource
     */
    def name: String
    
    /**
     * The methods this resource supports
     */
    def allowedMethods: Traversable[Method]
    
    /**
     * Performs an operation on this resource and forms a response. The resource may expect that 
     * this method will only be called with methods that are allowed by the resource.
     * @param request the request targeted to the resource
     * @param remainingPath if any of the path was left unfollowed by this resource earlier, it 
     * is provided here
     */
    def toResponse(request: Request, remainingPath: Option[Path])(implicit settings: ServerSettings): Response
    
    /**
     * Follows the path to a new resource. Returns a result suitable for the situation.
     */
    def follow(path: Path, headers: Headers, parameters: Model[Property], 
            cookies: Map[String, Cookie])(implicit settings: ServerSettings): ResourceSearchResult
}