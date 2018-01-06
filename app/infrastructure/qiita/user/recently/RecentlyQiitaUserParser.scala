package infrastructure.qiita.user.recently

import domain.qiita.user.QiitaUserName
import spray.json.DefaultJsonProtocol._
import spray.json._

private[user] final case class RecentlyQiitaUserParser(json: String) {
  def parse: Seq[QiitaUserName] = {
    val items = JsonParser(json).asInstanceOf[JsArray].elements
    items.map { item =>
      val name = item.asJsObject.getFields("id").head.convertTo[String]
      QiitaUserName(name)
    }
  }
}
