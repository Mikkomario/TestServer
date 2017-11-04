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
import http.Response
import java.io.OutputStream
import java.io.ByteArrayOutputStream
import http.Method.Get
import utopia.flow.parse.JSONReader

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
    
    def responseToString(response: Response) = 
    {
        if (response.writeBody.isDefined)
        {
            val out = new ByteArrayOutputStream()
            try
            {
                response.writeBody.get(out)
                Some(out.toString(response.headers.charset.map(_.name()).getOrElse("UTF-8")))
            }
            finally
            {
                out.close()
            }
        }
        else
        {
            None
        }
    }
    
    def responseToModel(response: Response) = responseToString(response).flatMap(JSONReader.parseSingle)
    
    def makeRequest(method: Method, path: Path, parameters: Model[Constant] = Model(Vector())) = 
    {
        new Request(method, path.toServerUrl, Some(path), parameters)
    }
    
    def getString(path: Path) = responseToString(handler(makeRequest(Get, path)))
    
    def getModel(path: Path) =  responseToModel(handler(makeRequest(Get, path)))
    
    println(getString(Path("rest")))
    println(getString(Path("rest", "root")))
    println(getString(Path("rest", "not_here")))
    
    println("Success!")
}