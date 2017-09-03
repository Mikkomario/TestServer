package http

import utopia.flow.datastructure.immutable.Model
import utopia.flow.datastructure.immutable.Constant
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ModelConvertible
import utopia.flow.generic.ValueConversions._
import utopia.flow.generic.FromModelFactory
import utopia.flow.datastructure.template
import utopia.flow.datastructure.template.Property

object Request extends FromModelFactory[Request]
{
    def apply(model: template.Model[Property]) = 
    {
        val method = model("method").string.flatMap { Method.parse }
        val path = model("path").string.map { Path.parse }
        
        if (method.isDefined && path.isDefined)
        {
            Some(Request(method.get, path.get, model("parameters").modelOr(), 
                    model("headers").model.flatMap(Headers.apply).getOrElse(Headers()), 
                    model("cookies").vectorOr().flatMap { _.model }.flatMap { Cookie(_) }))
        }
        else 
        {
            None
        }
    }
}

/**
 * A request represents an http request made from client side to server side. A request targets 
 * a single resource with an operation and may contain parameters, files and headers
 * @author Mikko Hilpinen
 * @since 3.9.2017
 */
case class Request(val method: Method, val path: Path, val parameters: Model[Constant] = Model(Vector()), 
        val headers: Headers = Headers(), val cookies: Seq[Cookie] = Vector()) extends ModelConvertible
{
    // IMPLEMENTED METHODS / PROPERTIES    -----
    
    override def toModel = Model(Vector("method" -> method.name, "path" -> path.toString(), 
            "parameters" -> parameters, "headers" -> headers.toModel, 
            "cookies" -> cookies.map { _.toModel }.toVector))
    
    
    // OTHER METHODS    ------------------------
    
    def cookieValue(cookieName: String) = cookies.find { _.name.toLowerCase() == 
            cookieName.toLowerCase() }.map { _.value }.getOrElse(Value.empty())
}