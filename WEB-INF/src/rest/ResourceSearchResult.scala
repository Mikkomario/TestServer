package rest

import http.NotFound
import http.Path
import http.Status
import http.Response
import http.Headers
import http.ContentCategory.Text
import java.nio.charset.Charset

/**
 * There are different types of results that can be get when following a path alongside resources. 
 * All of those result types are under this trait.
 */
sealed trait ResourceSearchResult

/**
 * Found means that the final resource has been found and the path doesn't need to be followed 
 * anymore
 */
final case class Found(val resource: Resource) extends ResourceSearchResult

/**
 * Follow means that the next resource was found but there is still some path to cover. A follow 
 * response should be followed by another search.
 * @param resource The next resource on the path
 * @param remainingPath The path remaining after the provided resource
 */
final case class Follow(val resource: Resource, remainingPath: Path) extends ResourceSearchResult

/**
 * A redirect is returned when a link is found and must be followed using a separate path
 * @param newPath The new path to follow to the original destination resource
 */
final case class Redirected(newPath: Path) extends ResourceSearchResult

/**
 * An error is returned when the next resource is not found or is otherwise not available
 */
final case class Error(val status: Status = NotFound, val message: Option[String] = None) extends ResourceSearchResult
{
    /* TODO: Finish once charsets are added to headers
    def toResponse(charSet: Charset) = 
    {
        val headers = (if (message.isDefined) Headers().withContentType(Text/"plain") else Headers()).withCurrentDate
        // TODO: Add encoding
        new Response(status, headers, Vector(), message.map { message => _.write(message.getBytes) })
    }*/
}