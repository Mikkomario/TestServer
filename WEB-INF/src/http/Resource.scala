package http

import utopia.flow.datastructure.template.Model
import utopia.flow.datastructure.template.Property

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
     */
    def toResponse(method: Method, parameters: Model[Property], headers: Headers, 
            cookies: Map[String, Cookie], uploads: Map[String, FileUpload]): Response
    
    /**
     * Follows the path to a new resource. Returns a result suitable for the situation.
     */
    def follow(path: Path, headers: Headers, parameters: Model[Property], 
            cookies: Map[String, Cookie]): ResourceSearchResult
}