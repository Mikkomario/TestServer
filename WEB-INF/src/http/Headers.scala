package http

import scala.collection.immutable.Map
import utopia.flow.generic.ModelConvertible
import utopia.flow.datastructure.immutable.Model
import utopia.flow.generic.ValueConversions._
import utopia.flow.generic.FromModelFactory
import utopia.flow.datastructure.template.Property
import utopia.flow.datastructure.template
import java.time.format.DateTimeFormatter
import scala.util.Try
import java.time.Instant
import java.time.ZonedDateTime
import java.time.ZoneOffset
import scala.collection.immutable.HashMap

object Headers extends FromModelFactory[Headers]
{   
    override def apply(model: template.Model[Property]) = 
    {
        val fields = model.attributesWithValue.map { property => (property.name, 
                property.value.vectorOr().flatMap { _.string }) }.toMap
        Some(new Headers(fields))
    }
}

/**
 * Headers represent headers used in html responses and requests
 * @author Mikko Hilpinen
 * @since 22.8.2017
 */
case class Headers(val fields: Map[String, Seq[String]] = HashMap()) extends ModelConvertible
{
    // IMPLEMENTED METHODS    -----
    
    def toModel = Model(fields.toVector.map { case (key, values) => (key, 
            values.toVector.map { _.toValue }.toValue) });
    
    
    // COMPUTED PROPERTIES    -----
    
    /**
     * The methods allowed for the server resource
     */
    def allowedMethods = apply("Allow").flatMap { Method.parse }
    
    /**
     * The content types accepted by the client
     */
    def acceptedTypes = apply("Accept").flatMap { ContentType.parse }
    
    /**
     * The type of the associated content. None if not defined.
     */
    def contentType = firstValue("Content-Type").flatMap { ContentType.parse }
    
    /**
     * The Date general-header field represents the date and time at which the message was 
     * originated, having the same semantics as orig-date in RFC 822. The field value is an 
     * HTTP-date, as described in section 3.3.1; it MUST be sent in RFC 1123 [8]-date format.
     */
    def date = timeHeader("Date")
    
    /**
     * Creates a new set of headers with the updated message date / time
     */
    def withCurrentDate = withDate(Instant.now())
    
    /**
     * The time when the resource was last modified
     */
    def lastModified = timeHeader("Last-Modified")
    
    
    // OPERATORS    ---------------
    
    /**
     * Finds the values associated with the specified header name. If there are no values, returns 
     * an empty collection
     */
    def apply(headerName: String) = fields.getOrElse(headerName, Vector())
    
    /**
     * Adds new values to a header. Will not overwrite any existing values.
     */
    def +(headerName: String, values: Seq[String]) = 
    {
        if (fields.contains(headerName))
        {
            // Appends to existing values
            val newValues = apply(headerName) ++ values
            Headers(fields + (headerName -> newValues))
        }
        else
        {
            withHeader(headerName, values)
        }
    }
    
    /**
     * Adds a new value to a header. Will not overwrite any existing values.
     */
    def +(headerName: String, value: String): Headers = this + (headerName, Vector(value))
    
    
    
    // OTHER METHODS    -----------
    
    /**
     * The first value associated with the specified header name
     */
    def firstValue(headerName: String) = fields.get(headerName).flatMap { _.headOption }
    
    /**
     * Returns a copy of these headers with a new header. Overwrites any previous values on the 
     * targeted header.
     */
    def withHeader(headerName: String, values: Seq[String]) = new Headers(fields + (headerName -> values))
    
    /**
     * Returns a copy of these headers with a new header. Overwrites any previous values on the 
     * targeted header.
     */
    def withHeader(headerName: String, value: String, more: String*): Headers = 
            withHeader(headerName, value +: more);
    
    /**
     * Parses a header field into a time instant
     */
    def timeHeader(headerName: String) = firstValue(headerName).flatMap { dateStr => 
            Try(Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(dateStr))).toOption }
    
    /**
     * Parses an instant into correct format and adds it as a header value. Overwrites a previous 
     * version of that header, if there is one.
     */
    def withTimeHeader(headerName: String, value: Instant) = withHeader(headerName, 
            DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(value, ZoneOffset.UTC)));
    
    /**
     * Checks whether a method is allowed for the server side resource
     */
    def allows(method: Method) = allowedMethods.contains(method)
    
    /**
     * Overwrites the list of methods allowed to be used on the targeted resource
     */
    def withAllowedMethods(methods: Seq[Method]) = withHeader("Allow", methods.map { _.toString })
    
    /**
     * Adds a new method to the methods allowed for the targeted resource
     */
    def withMethodAllowed(method: Method) = this + ("Allow", method.toString)
    
    /**
     * Checks whether the client accepts the provided content type
     */
    def accepts(contentType: ContentType) = acceptedTypes.contains(contentType)
    
    /**
     * Overwrites the set of accepted content types
     */
    def withAcceptedTypes(types: Seq[ContentType]) = withHeader("Accept", types.map { _.toString })
    
    /**
     * Adds a new content type to the list of content types accepted by the client
     */
    def withTypeAccepted(contentType: ContentType) = this + ("Accept", contentType.toString)
    
    /**
     * Creates a new headers with the content type specified
     */
    def withContentType(contentType: ContentType) = this + ("Content-Type", contentType.toString)
    
    /**
     * Creates a new header with the time when the message associated with this header was originated. 
     * If the message was just created, you may wish to use #withCurrentDate
     */
    def withDate(time: Instant) = withTimeHeader("Date", time);
    
    /**
     * Creates a new header with the time when the resource was last modified
     */
    def withLastModified(time: Instant) = withTimeHeader("Last-Modified", time)
    
    // TODO: Implement support for following predefined headers:
    /*
     * - Accept-Charset
     * - Accept-Language (?)
     * - Cookie
     * - Content-Length (?)
     * - Content-Encoding
     * - Content-Language
     * - Expires (?)
     * - Location
     * - Set-Cookie
     */
}