package rest

import collection.JavaConverters._
import utopia.flow.generic.ValueConversions._
import utopia.flow.datastructure.immutable
import java.nio.file

import http.Method.Get
import http.Path
import http.Headers
import utopia.flow.datastructure.template.Model
import utopia.flow.datastructure.template.Property
import http.Cookie
import http.ServerSettings
import http.Request
import http.Response
import java.io.File
import java.nio.file.Files
import http.FileUpload
import scala.util.Try
import scala.util.Failure

/**
 * This resource is used for uploading and retrieving file data
 * @author Mikko Hilpinen
 * @since 17.9.2017
 */
class FilesResource(override val name: String) extends Resource
{
    // IMPLEMENTED METHODS & PROPERTIES    ---------------------
    
    override def allowedMethods = Vector(Get)
    
    override def follow(path: Path, headers: Headers, parameters: Model[Property], 
            cookies: Map[String, Cookie])(implicit settings: ServerSettings) = Ready(Some(path));
    
    // TODO: Add traversed path so that post location can be provided        
    override def toResponse(request: Request, remainingPath: Option[Path])(implicit settings: ServerSettings) = 
    {
        val targetFilePath = remainingPath.map { remaining => 
                settings.uploadPath.resolve(remaining.toString) }.getOrElse(settings.uploadPath);
        
        if (Files.isDirectory(targetFilePath))
        {
            Response.fromModel(makeDirectoryModel(targetFilePath.toFile(), request.targetUrl))
        }
        else
        {
            Response.fromFile(targetFilePath)
        }
    }
    
    
    // OTHER METHODS    ---------------------------------------
    
    /**
     * @param directory the directory whose data is returned
     * @param directoryAddress the request url targeting the directory
     */
    private def makeDirectoryModel(directory: File, directoryAddress: String) = 
    {
        val allFiles = directory.listFiles().toSeq.groupBy { _.isDirectory() }
        val files = allFiles.getOrElse(false, Vector()).map { directoryAddress + "/" + _.getName }
        val directories = allFiles.getOrElse(true, Vector()).map { directoryAddress + "/" + _.getName }
        
        immutable.Model(Vector("files" -> files.toVector, "directories" -> directories.toVector))
    }
    
    private def upload(fileUpload: FileUpload, remainingPath: Option[Path])(implicit settings: ServerSettings) = 
    {
        val makeDirectoryResult = remainingPath.map { remaining => 
                Try(Files.createDirectories(settings.uploadPath.resolve(remaining.toString()))) }
        
        if (makeDirectoryResult.isEmpty || makeDirectoryResult.get.isSuccess)
        {
            val fileName = remainingPath.map { _ / fileUpload.submittedFileName }.getOrElse(
                    Path(fileUpload.submittedFileName));
            fileUpload.write(fileName)
        }
        else
        {
            Failure(makeDirectoryResult.get.failed.get)
        }
    }
}