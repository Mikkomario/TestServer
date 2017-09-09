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
     * @return the parsed path. None if there was no parts on the path
     */
    def parse(pathString: String) = 
    {
        val path = Path(pathString.split("/").filterNot { _.isEmpty() })
        if (path.parts.isEmpty) None else Some(path)
    }
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
    def tail = drop(1)
    
    
    // IMPLEMENTED METHODS    -----------------
    
    override def toString = parts.reduceLeftOption { _ + "/" + _ }.getOrElse("")
    
    
    // OPERATORS    ---------------------------
    
    /**
     * Creates a new path with the specified path added to the end
     */
    def /(path: Path) = Path(parts ++ path.parts)
    
    /**
     * Creates a new path with the specified element appended to the end
     */
    def /(element: String): Path = Path.parse(element).map(/).getOrElse(this) 
    
    
    // OTHER METHODS    -----------------------
    
    /**
     * Creates a new path with the specified path prepended to the beginning
     */
    def prepend(path: Path) = Path(path.parts ++ parts)
    
    /**
     * Creates a new path with the specified element prepended to the beginning
     */
    def prepend(element: String): Path = Path.parse(element).map(prepend).getOrElse(this)
    
    /**
     * Drops the first n element from this path and returns the result
     */
    def drop(n: Int) = if (n >= parts.size) None else Some(Path(parts.drop(n)))
}