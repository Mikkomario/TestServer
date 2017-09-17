package http

/**
 * Server settings specify values commonly used by server-side resources
 * @author Mikko Hilpinen
 * @since 17.9.2017
 */
// TODO: Add parameter encoding as well
case class ServerSettings(val address: String, val uploadPath: java.nio.file.Path)