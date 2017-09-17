package http

import java.nio.file
import java.io.InputStream
import java.io.FileInputStream
import scala.util.Try


/**
 * Instances of this class are used for representing files the client sends to the server.<br>
 * The instance is not internally immutable, since it uses streams, but attempts to have value 
 * semantics in relation to the user.
 * @author Mikko Hilpinen
 * @since 7.9.2017
 */
class FileUpload(val name: String, val sizeBytes: Long, 
        val contentType: ContentType, val submittedFileName: String, 
        getInputStream: => InputStream, writeToFile: String => Unit)
        (private implicit val settings: ServerSettings)
{
    // ATTRIBUTES    ----------------------------
    
    // The path to where the file has been permanently (?) stored
    private var fileSavePath: Option[file.Path] = None
    
    
    // COMPUTED PROPERTIES    ------------------
    
    def toInputStream = fileSavePath.map { path => Try(new FileInputStream(path.toFile())).orElse(
            Try(getInputStream)) }.getOrElse(Try(getInputStream));
    
    
    // OTHER METHODS    -------------------------
    
    def write(fileName: String = submittedFileName) = Try({
        if (fileSavePath.isEmpty) 
        {
            writeToFile(fileName)
            fileSavePath = Some(settings.uploadPath.resolve(fileName))
        }
        
        fileSavePath.get
    })
}