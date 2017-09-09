package rest

import http.Request

/**
 * This class handles a request by searching for the targeted resource and performing the right 
 * operation on the said resource
 * @author Mikko Hilpinen
 * @since 9.9.2017
 */
class RequestHandler(val rootResources: Traversable[Resource])
{
    def apply(request: Request) = 
    {
        // Finds the initial resource for the path
        
    }
}