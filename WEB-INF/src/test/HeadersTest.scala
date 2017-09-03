package test

import utopia.flow.generic.DataType
import http.ContentCategory._
import http.Headers
import http.Method.Get
import http.Method.Post
import http.Method.Delete
import http.Method.Put
import java.time.Instant

/**
 * This app tests use of headers
 */
object HeadersTest extends App
{
    DataType.setup()
    
    val contentType = Text/"plain"
    val empty = Headers()
    
    assert(empty.withContentType(contentType).contentType.exists { _ == contentType })
    
    val allowed = Vector(Get, Post)
    val allowHeaders = empty.withAllowedMethods(allowed)
    
    assert(allowHeaders.allowedMethods == allowed)
    assert(allowHeaders.allows(Get))
    assert(allowHeaders.withMethodAllowed(Delete).allows(Delete))
    assert(!allowHeaders.allows(Delete))
    
    val epoch = Instant.EPOCH
    
    assert(empty.withDate(epoch).date.exists { _ == epoch })
    
    assert(Headers(allowHeaders.toModel).exists { _ == allowHeaders })
    
    println("Success!")
}