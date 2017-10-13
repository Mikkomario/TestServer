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
import rest.Ready
import utopia.flow.generic.ModelType
import http.Created
import http.BadRequest

/**
 * This resource works as a mutable model that accepts rest commands
 * @author Mikko Hilpinen
 * @since 10.10.2017
 */
class TestRestResource(val name: String, initialValues: template.Model[Constant] = Model(Vector())) extends Resource
{
    // ATTRIBUTES    -----------------
    
    override val allowedMethods = Vector(Get)
    
    // All initial values with model type are transformed into children
    private var children = initialValues.attributes.flatMap(attribute => 
            attribute.value.model.map(attribute.name -> _)).map { case (name, model) => 
            new TestRestResource(name, model) }
    // The other values are stored as local values
    private var values = initialValues.attributes.filter(_.value.model.isEmpty)
    
    
    // IMPLEMENTED METHODS    --------
    
    override def toResponse(request: Request, remainingPath: Option[Path])(implicit settings: ServerSettings) = 
    {
        request.method match 
        {
            case Get => handleGet(request.path)
            case Post => 
                if (request.path.isEmpty) 
                    Response.plainText("Path required", BadRequest) 
                else 
                    handlePost(request.path.get, request.parameters)
            case _ => Response.empty(NotImplemented)
        }
    }
    
    override def follow(path: Path, request: Request)(implicit settings: ServerSettings) = 
    {
        // If the path leads to a child, forwards the request to that resource
        val targetName = path.head.toLowerCase()
        val nextResource = children.find(_.name.toLowerCase() == targetName)
        val remainingPath = path.tail
        
        nextResource match 
        {
            // For some methods, stops if the next element would be at the end of the path
            case Some(next) => 
                if ((request.method == Delete || request.method == Post) && remainingPath.isEmpty) 
                    Ready(Some(path)) 
                else 
                    Follow(next, remainingPath)
            case None => Error()
        }
    }
    
    
    // OTHER METHODS    -------------
    
    // Wraps values into a model and Displays the children as links
    private def handleGet(path: Option[Path])(implicit settings: ServerSettings) = Response.fromModel(
            new Model[Constant](values ++ children.map(child => 
            new Constant(child.name, (path/child.name).toServerUrl.toValue))));
    
    private def handlePost(path: Path, parameters: template.Model[Constant])
            (implicit settings: ServerSettings) = 
    {
        children :+= new TestRestResource(path.lastElement, parameters)
        Response.empty(Created).withModifiedHeaders(_.withLocation(path.toServerUrl))
    }
}