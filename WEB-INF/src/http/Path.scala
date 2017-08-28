package http

import utopia.flow.datastructure.immutable.Tree

object Path
{
    // OPERATORS    ----------------------
    
    /**
     * Creates a path from a set of ordered strings
     */
    def apply(first: String, more: String*) = new Path(first +: more)
    
    
    // OTHER METHODS    ------------------
    
    /**
     * Parses a path from a string representation
     * @param pathString a string representation of a path. Eg. 'foo/bar'
     */
    def parse(pathString: String) = Path(pathString.split("/"))
}

/**
 * Paths are used for determining a location of a resource on server side
 * @author Mikko Hilpinen
 * @since 22.8.2017
 */
case class Path(val parts: Seq[String])
{
    // COMPUTED PROPERTIES    -----------------
    
    /**
     * The first string element in the path
     */
    def head = parts.head
    
    /**
     * The remaining portion of the path after the first element
     */
    def tail = if (parts.size > 1) Some(Path(parts.tail)) else None
    
    
    // IMPLEMENTED METHODS    -----------------
    
    override def toString = parts.reduceLeftOption { _ + "/" + _ }.getOrElse("")
    
    
    // OPERATORS    ---------------------------
    
    /**
     * Creates a new path with the specified element appended to the end
     */
    def /(element: String) = Path(parts :+ element)
    
    
    // OTHER METHODS    -----------------------
    
    /**
     * Creates a new path with the specified element prepended to the beginning
     */
    def prepend(element: String) = Path(element +: parts)
}