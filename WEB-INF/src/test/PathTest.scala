package test

import http.Path

/**
 * This app tests use of Paths
 * @author Mikko Hilpinen
 * @since 22.8.2017
 */
object PathTest extends App
{
    val path = Path("a", "b", "c")
    
    assert(path == Path(Vector("a", "b", "c")))
    assert(path.head == "a")
    assert(path.tail.exists { _ == Path("b", "c") })
    assert(path.tail.get.tail.get.tail.isEmpty)
    assert(path/"d" == Path("a", "b", "c", "d"))
    assert(path.prepend("x") == Path("x", "a", "b", "c"))
    assert(Path.parse(path.toString) == path)
    
    println("Success!")
}