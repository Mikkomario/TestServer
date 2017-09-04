package http

trait Resource
{
    // ABSTRACT PROPERTIES & METHODS ------------------
    
    /**
     * The methods this resource supports
     */
    def allowedMethods: Traversable[Method]
    
    /**
     * Finds the next resource while moving along the provided path
     * @path a path starting from this resource
     * @return the next resource along the path or None if no such resource was found
     */
    def findNext(path: Path): Option[Resource]
    
    // def perform(request: Request): Response
    
    
    // OTHER METHODS    ------------------------------
    
    /**
     * Finds the resource at the end of the provided path, if there is one
     * @path a path starting from this resource
     * @return the resource at the end of the path that starts from this resource. None if no 
     * such resource could be found
     */
    def find(path: Path): Option[Resource] = 
    {
        lazy val remainingPath = path.tail
        findNext(path).flatMap { next => 
                if (remainingPath.isDefined) next.find(remainingPath.get) else Some(next) }
    }
}