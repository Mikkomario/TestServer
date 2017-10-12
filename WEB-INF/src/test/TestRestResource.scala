package test

import utopia.flow.generic.ValueConversions._

import utopia.flow.datastructure.template

import utopia.flow.datastructure.immutable.Model
import utopia.flow.datastructure.immutable.Constant
import http.Method.Get
import http.Method.Post
import http.Method.Put
import http.Method.Delete
import rest.Resource
import http.Request
import http.Path
import http.ServerSettings
import http.Headers
import utopia.flow.datastructure.template.Property
import http.Cookie
import utopia.flow.datastructure.immutable.Value
import rest.Follow
import rest.Error
import http.Response
import http.NotImplemented

/**
 * This resource works as a mutable model that accepts rest commands
 * @author Mikko Hilpinen
 * @since 10.10.2017
 */
class TestRestResource(val name: String, private var values: Map[String, Value] = Map()) extends Resource
{
    // ATTRIBUTES    -----------------
    
    override val allowedMethods = Vector(Get)
    
    private var children = Vector[TestRestResource]()
    
    
    // IMPLEMENTED METHODS    --------
    
    override def toResponse(request: Request, remainingPath: Option[Path])(implicit settings: ServerSettings) = 
    {
        request.method match 
        {
            case Get => handleGet(request.path)
            case _ => Response.empty(NotImplemented)
        }
    }
    
    override def follow(path: Path, headers: Headers, parameters: template.Model[Property], 
            cookies: Map[String, Cookie])(implicit settings: ServerSettings) = 
    {
        // If the path leads to a child, forwards the request to that resource
        val targetName = path.head.toLowerCase()
        val nextResource = children.find(_.name.toLowerCase() == targetName)
        
        nextResource match 
        {
            case Some(next) => Follow(next, path.tail)
            case None => Error()
        }
    }
    
    
    // OTHER METHODS    -------------
    
    // Wraps the values and children into a model
    private def handleGet(path: Option[Path])(implicit settings: ServerSettings) = Response.fromModel(
            Model(values.toVector ++ children.map(child => 
            (child.name, (path/child.name).toServerUrl.toValue))));
    
    private def handlePost(path: Option[Path], parameters: template.Model[Property]) = 
    {
        
    }
}