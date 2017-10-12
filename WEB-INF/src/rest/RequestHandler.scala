package rest

import utopia.flow.generic.ValueConversions._
import http.Request
import http.Path
import http.Response
import http.MethodNotAllowed
import http.Headers
import http.Method
import utopia.flow.datastructure.immutable.Model
import http.ServerSettings

/**
 * This class handles a request by searching for the targeted resource and performing the right 
 * operation on the said resource
 * @author Mikko Hilpinen
 * @since 9.9.2017
 */
class RequestHandler(val childResources: Traversable[Resource], val path: Option[Path] = None)(implicit val settings: ServerSettings)
{
    // COMPUTED PROPERTIES    -------------
    
    private def currentDateHeader = Headers().withCurrentDate
    
    private def get = 
    {
        val childLinks = childResources.map { child => (child.name, (settings.address + "/" + 
                path.map { _/(child.name).toString() }.getOrElse(child.name)).toValue) }
        Response.fromModel(Model(childLinks))
    }
    
    
    // OPERATORS    -----------------------
    
    /**
     * Forms a response for the specified request
     */
    def apply(request: Request) = handlePath(request, request.path)
    
    
    // OTHER METHODS    -------------------
    
    private def handlePath(request: Request, targetPath: Option[Path]): Response = 
    {
        // Parses the target path (= request path - handler path)
        var remainingPath = targetPath
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
        
        val firstResource = remainingPath.map{ _.head }.flatMap { resourceName => 
                    childResources.find { _.name.equalsIgnoreCase(resourceName) } }
        if (firstResource.isEmpty)
        {
            error = Some(Error())
        }
        
        // Case: Error
        if (error.isDefined)
        {
            error.get.toResponse()
        }
        else if (remainingPath.isEmpty)
        {
            // Case: RequestHandler was targeted
            get
        }
        else
        {
            // Case: A resource under the handler was targeted
            // Finds the initial resource for the path
            var lastResource = remainingPath.map{ _.head }.flatMap { resourceName => 
                    childResources.find { _.name.equalsIgnoreCase(resourceName) } }
            if (lastResource.isDefined)
            {
                // Drops the first resource from the remaining path
                remainingPath = remainingPath.flatMap { _.tail }
            }
            
            // var cachedResources = Vector[Resource]()
            var foundTarget = remainingPath.isEmpty
            var redirectPath: Option[Path] = None
            
            // Searches as long as there is success and more path to discover
            while (lastResource.isDefined && remainingPath.isDefined && error.isEmpty && 
                    !foundTarget && redirectPath.isEmpty)
            {
                // cachedResources :+= lastResource.get
                
                // Sees what's the resources reaction
                val result = lastResource.get.follow(remainingPath.get, request.headers, 
                        request.parameters, request.cookies);
                result match
                {
                    case Ready(remaining) => 
                    {
                        foundTarget = true
                        remainingPath = remaining
                    }
                    case Follow(next, remaining) => 
                    {
                        lastResource = Some(next)
                        remainingPath = remaining
                    }
                    case Redirected(newPath) => redirectPath = Some(newPath)
                    case foundError: Error => error = Some(foundError)
                }
            }
            
            // Handles search results
            if (error.isDefined)
            {
                // TODO: Use correct charset
                error.get.toResponse()
            }
            else if (redirectPath.isDefined)
            {
                handlePath(request, redirectPath)
            }
            else if (foundTarget)
            {
                // Makes sure the method can be used on the targeted resource
                val allowedMethods = lastResource.get.allowedMethods
                
                if (allowedMethods.exists(==))
                {
                    lastResource.get.toResponse(request, remainingPath)
                }
                else
                {
                    val headers = Headers().withCurrentDate.withAllowedMethods(allowedMethods.toVector)
                    new Response(MethodNotAllowed, headers)
                }
            }
            else
            {
                Error().toResponse()
            }
        }
    }
    
    private def makeNotAllowedResponse(allowedMethods: Seq[Method]) = new Response(
            MethodNotAllowed, currentDateHeader.withAllowedMethods(allowedMethods))
}