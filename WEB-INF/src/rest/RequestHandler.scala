package rest

import utopia.flow.generic.ValueConversions._
import http.Request
import http.Path
import http.Response
import http.MethodNotAllowed
import http.Headers
import http.Method
import utopia.flow.datastructure.immutable.Model

/**
 * This class handles a request by searching for the targeted resource and performing the right 
 * operation on the said resource
 * @author Mikko Hilpinen
 * @since 9.9.2017
 */
class RequestHandler(val serverAddress: String, val childResources: Traversable[Resource], 
        val path: Option[Path] = None)
{
    // COMPUTED PROPERTIES    -------------
    
    private def currentDateHeader = Headers().withCurrentDate
    
    private def get = 
    {
        val childLinks = childResources.map { child => (child.name, (serverAddress + "/" + 
                path.map { _/(child.name).toString() }.getOrElse(child.name)).toValue) }
        Response.fromModel(Model(childLinks))
    }
    
    
    // OPERATORS    -----------------------
    
    def apply(request: Request) = 
    {
        // Parses the target path (= request path - handler path)
        var remainingPath = request.path
        var error: Option[Error] = None
        var pathToSkip = path
        
        while (pathToSkip.isDefined && error.isEmpty)
        {
            if (remainingPath.isEmpty || !remainingPath.get.head.equalsIgnoreCase(pathToSkip.get.head))
            {
                error = Some(Error())
            }
            else
            {
                remainingPath = remainingPath.get.tail
                pathToSkip = pathToSkip.get.tail
            }
        }
        
        // Handles case where the requestHandler is targeted
        
        // Finds the initial resource for the path
        var nextResource = remainingPath.map{ _.head }.flatMap { resourceName => 
                childResources.find { _.name.equalsIgnoreCase(resourceName) } }
        var cachedResources = Vector[Resource]()
        
        // Searches as long as there is success and more path to discover
        /*
        while (nextResource.isDefined && remainingPath.isDefined)
        {
            cachedResources :+= nextResource.get
        }*/
    }
    
    
    // OTHER METHODS    -------------------
    
    private def makeNotAllowedResponse(allowedMethods: Seq[Method]) = new Response(
            MethodNotAllowed, currentDateHeader.withAllowedMethods(allowedMethods))
}