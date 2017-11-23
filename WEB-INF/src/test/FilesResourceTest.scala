package test

import http.ServerSettings
import utopia.flow.generic.DataType
import java.nio.file.Paths
import rest.FilesResource
import rest.RequestHandler
import http.Path
import http.Request
import http.Method.Post
import http.FileUpload

/**
 * This test makes sure the FilesResource class is working correctly
 */
object FilesResourceTest extends App
{
    DataType.setup()
    
    // Creates the main resources first
    implicit val settings = ServerSettings("https://localhost:9999", Paths.get("D:/Uploads"))
    
    val rootResource = new FilesResource("root")
    val handler = new RequestHandler(Vector(rootResource), Some(Path("rest")))
    
    def makePostRequest(path: Path, uploads: Vector[FileUpload]) = new Request(method = Post, 
            targetUrl = path.toServerUrl, path = Some(path), rawFileUploads = uploads)
}