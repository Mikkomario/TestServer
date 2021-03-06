package http

import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.ModelConvertible
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ValueConversions._
import utopia.flow.generic.FromModelFactory
import utopia.flow.datastructure.template
import utopia.flow.datastructure.template.Property

object Cookie extends FromModelFactory[Cookie]
{
    /**
     * Parses a cookie from the provided model. The model must have a 'name' property or None is 
     * returned.
     */
    override def apply(model: template.Model[Property]) = 
    {
        // Name property is required
        val name = model("name").string
        
        if (name.isDefined)
        {
            Some(Cookie(name.get, model("value"), model("life_limit_seconds").int, 
                    model("secure").booleanOr(false)))
        }
        else
        {
            None
        }
    }
}

/**
 * Cookies are used for storing data on the client side. The client should send cookies back to 
 * the server on the consequent requests.
 * @author Mikko Hilpinen
 * @since 3.9.2017
 */
case class Cookie(val name: String, val value: Value, val lifeLimitSeconds: Option[Int] = None, 
        val isSecure: Boolean = false) extends ModelConvertible
{
    // IMPLEMENTED METHODS / PROPERTIES    ------------
    
    override def toModel = Model(Vector("name" -> name, "value" -> value, 
            "life_limit_seconds" -> lifeLimitSeconds, "secure" -> isSecure));
}