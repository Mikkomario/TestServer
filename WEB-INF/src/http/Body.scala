package http

import utopia.access.http.Headers
import utopia.access.http.ContentType

/**
* This trait represents a request body or a part of that body (in case of multipart requests)
* @author Mikko Hilpinen
* @since 12.5.2018
**/
trait Body
{
    /**
     * The headers for this body
     */
	def headers: Headers
	/**
	 * The content length for this body. May be undefined
	 */
	def contentLength: Option[Long]
	/**
	 * The content type for this body
	 */
	def contentType: ContentType
}