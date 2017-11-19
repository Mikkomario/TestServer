package test

import utopia.flow.generic.ValueConversions._

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
import http.OK
import http.NotFound
import utopia.flow.datastructure.immutable.Value
import http.Method.Post
import http.Method.Put
import http.Created
import http.Method.Delete

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
    
    def stringToModel(s: String) = JSONReader.parseSingle(s)
    
    def responseToModel(response: Response) = responseToString(response).flatMap(stringToModel)
    
    def makeRequest(method: Method, path: Path, parameters: Model[Constant] = Model(Vector())) = 
    {
        new Request(method, path.toServerUrl, Some(path), parameters)
    }
    
    def getString(path: Path) = responseToString(handler(makeRequest(Get, path)))
    
    def getModel(path: Path) =  responseToModel(handler(makeRequest(Get, path)))
    
    def testModelExists(path: Path) = 
    {
        val response = handler(makeRequest(Get, path))
        assert(response.status == OK)
        assert(responseToModel(response).isDefined)
    }
    
    def testNotFound(path: Path) = assert(handler(makeRequest(Get, path)).status == NotFound)
    
    def testAttributeExists(path: Path, attName: String) = 
    {
        val response = handler(makeRequest(Get, path))
        val responseString = responseToString(response)
        
        if (response.status == OK)
        {
            assert(responseString.isDefined)
            
            println(responseString.get)
            assert(stringToModel(responseString.get).exists(_(attName).isDefined))
        }
        else 
        {
            println(s"Status: ${ response.status }")
            assert(false)
        }
    }
    
    def testPutAttribute(path: Path, attName: String, value: Value) = 
    {
        val response = handler(makeRequest(Put, path, Model(Vector(attName -> value))))
        assert(response.status == OK)
    }
    
    def testPostModel(path: Path, model: Model[Constant]) = 
    {
        val response = handler(makeRequest(Post, path, model))
        assert(response.status == Created)
        response.headers.location.foreach(println)
    }
    
    def testDelete(path: Path) = 
    {
        val response = handler(makeRequest(Delete, path))
        assert(response.status == OK)
    }
    
    def testAttValue(path: Path, attName: String, expectedValue: Value) = 
    {
        assert(getModel(path).exists(_(attName) == expectedValue))
    }
    
    testAttributeExists(Path("rest"), "root")
    
    val rootPath = Path("rest", "root")
    testModelExists(rootPath)
    testNotFound(Path("rest", "not_here"))
    
    testPutAttribute(rootPath, "att1", 1)
    testPutAttribute(rootPath, "att2", "test2")
    testPutAttribute(rootPath, "model", Model(Vector("a" -> 1, "b" -> 2)))
    
    testAttributeExists(rootPath, "att1")
    testAttributeExists(rootPath, "model")
    
    val model2Path = rootPath/"model2"
    
    testPostModel(model2Path, Model(Vector("test1" -> "test", "test2" -> 2)))
    testModelExists(model2Path)
    
    testAttValue(model2Path, "test1", "test")
    testPutAttribute(model2Path, "test1", 1)
    testAttValue(model2Path, "test1", 1)
    
    testDelete(model2Path)
    testNotFound(model2Path)
    
    println("Success!")
}