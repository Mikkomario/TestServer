package rest

import http.NotFound
import http.Path
import http.Status
import http.Response
import http.Headers
import http.ContentCategory.Text
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * There are different types of results that can be get when following a path alongside resources. 
 * All of those result types are under this trait.
 */
sealed trait ResourceSearchResult

/**
 * Ready means that the resource is ready to fulfil the request and form the response
 * @param remainingPath the path that is still left to cover, if there is any
 */
final case class Ready(val remainingPath: Option[Path] = None) extends ResourceSearchResult

/**
 * Follow means that the next resource was found but there is still some path to cover. A follow 
 * response should be followed by another search.
 * @param resource The next resource on the path
 * @param remainingPath The path remaining after the provided resource
 */
final case class Follow(val resource: Resource, val remainingPath: Path) extends ResourceSearchResult

/**
 * A redirect is returned when a link is found and must be followed using a separate path
 * @param newPath The new path to follow to the original destination resource
 */
final case class Redirected(val newPath: Path) extends ResourceSearchResult

/**
 * An error is returned when the next resource is not found or is otherwise not available
 */
final case class Error(val status: Status = NotFound, val message: Option[String] = None) extends ResourceSearchResult
{
    def toResponse(charset: Charset = StandardCharsets.UTF_8) = 
    {
        val headers = (if (message.isDefined) Headers().withContentType(Text/"plain", 
                Some(charset)) else Headers()).withCurrentDate
        new Response(status, headers, Vector(), message.map { message => _.write(message.getBytes(charset)) })
    }
}

// TODO: Add contextRequest