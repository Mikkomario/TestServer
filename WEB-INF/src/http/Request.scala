package http

import utopia.flow.datastructure.immutable.Model
import utopia.flow.datastructure.immutable.Constant

/**
 * A request represents an http request made from client side to server side. A request targets 
 * a single resource with an operation and may contain parameters, files and headers
 * @author Mikko Hilpinen
 * @since 3.9.2017
 */
class Request(val method: Method, val path: Path, val parameters: Model[Constant], headers: Headers)
{
    
}