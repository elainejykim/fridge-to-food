package fridge.http

import java.net.URLEncoder

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, RawHeader}

object Search {
  // import fridge.akka.AkkaManager._
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher



  def request(userQuery: String): HttpRequest = {
    // Request Parameters
    val display = 10
    val start = 1

    // Request Info
    val url = "https://openapi.naver.com/v1/search/encyc.json"
    val clientId = "Qud3Z3xub5BWci2OcQ9Z"
    val clientSecret = "KAX1A5c1fk"

    // convert string to UTF-8
    val query = URLEncoder.encode(userQuery, "UTF-8")
    // val query = userQuery.getBytes("UTF-8")

    val header = Seq(
      RawHeader("X-Naver-Client-Id", s"$clientId"),
      RawHeader("X-Naver-Client-Secret", s"$clientSecret")
    )

    val request = HttpRequest(
      HttpMethods.GET,
      url + s"?query=$query&display=$display&start=$start&sort=sim",
    ).withHeaders(header)

    request
  }

  def sendRequest(items: String): Future[String] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request(items))
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap {
      response => response.entity.toStrict(2.seconds)
    }

    entityFuture.map(entity => entity.data.utf8String)
  }

  def main(args: Array[String]): Unit = {
    sendRequest("양파, 버섯, 마늘").foreach(println)
  }

}