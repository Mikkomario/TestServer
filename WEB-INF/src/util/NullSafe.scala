package util

/**
 * This object contains an extension that allows safer handling of java-originated objects 
 * that may possibly contain null values
 * @author Mikko Hilpinen
 * @since 28.8.2017
 */
object NullSafe
{
    // TODO: Possibly move this feature to utopia flow since it's that general
    
    implicit class NullOption[T <: Object](val obj: T) extends AnyVal
    {
        /**
         * Performs a null check and returns an optional non-null value. None is returned for 
         * null values.
         */
        def toOption = if (obj == null) None else Some(obj)
    }
}