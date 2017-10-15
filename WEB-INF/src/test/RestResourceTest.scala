package test

import utopia.flow.generic.DataType
import rest.RequestHandler
import http.Path
import http.ServerSettings
import java.nio.file.Paths
import http.Request
import http.Headers
import http.Method
import utopia.flow.datastructure.immutable.Model
import utopia.flow.datastructure.immutable.Constant

/**
 * This test makes sure the rest test resource and the request handler are working
 */
object RestResourceTest extends App
{
    DataType.setup()
    
    // Creates the main resources first
    implicit val settings = ServerSettings("https://localhost:9999", Paths.get("D:/Uploads"))
    
    val rootResource = new TestRestResource("root")
    val handler = new RequestHandler(Vector(rootResource), Some(Path("rest")))
    
    def makeRequest(method: Method, path: Path, parameters: Model[Constant] = Model(Vector())) = 
    {
        new Request(method, path.toServerUrl, Some(path), parameters)
    }
    
    // def getModel(path: Path) =  
    
    println("Success!")
}