package models

import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.Play.current
import scala.concurrent.{ExecutionContext, Future}

case class ShortUrl(hash: String, fullUrl: String)

object ShortUrl {
  implicit val fmt = Json.format[ShortUrl]

  def lookup(hash: String)(implicit ec: ExecutionContext): Future[Option[ShortUrl]] = {
    ShortUrlDAO.findByHash(hash) map { oFullUrl =>
      oFullUrl.map { fullUrl =>
        ShortUrl(hash, fullUrl)
      }
    }
  }

  def shorten(fullUrl: String)(implicit ec: ExecutionContext): Future[ShortUrl] = {
    nextUniqueId map { nextId =>
      hashId(nextId)
    } flatMap  {hash =>
      ShortUrlDAO.saveMapping(hash, fullUrl) map { _ =>
        ShortUrl(hash, fullUrl)
      }
    }
  }

  val countioSeviceUrl = "http://count.io/vb/urlshortener/hash+"
  val countioRequestHolder = WS.url(countioSeviceUrl)

  private def nextUniqueId(implicit ec: ExecutionContext): Future[Int] = {
    countioRequestHolder.post("") map { response =>
      (response.json \ "count").as[Int]
    }
  }

  private val hashIndex = "asdfasdf"
  private def hashId(id: Int, acc: String = ""): String =
    if (id == 0)
      acc
    else
      hashId(id / hashIndex.length, acc + hashIndex.charAt(id % hashIndex.length))

}
