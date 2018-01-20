package infrastructure.qiita.article

import javax.inject.Singleton

import domain.qiita.article.json.RawPropsArticleJson
import domain.qiita.article.{QiitaItemId, _}
import domain.qiita.user.CrawledDateTime
import scalikejdbc._

@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.DefaultArguments", "org.wartremover.warts.Nothing"))
@Singleton
final class ScalikejdbcQiitaRawPropsArticleJsonRepository extends QiitaRawPropsArticleJsonRepository {
  override def register(itemId: QiitaItemId, rawJson: RawPropsArticleJson, crawled: CrawledDateTime)(implicit session: DBSession = AutoSession): Unit = {
    val qiitaItemId     = itemId.value
    val rawArticleJson  = rawJson.value
    val crawledDateTime = crawled.value

    sql"""
          INSERT INTO raw_qiita_props_article_jsons (item_id, raw_json, crawled_date_time)
          VALUES ($qiitaItemId, $rawArticleJson, $crawledDateTime);
       """
      .update()
      .apply()

    () // 明示的に Unit を返す
  }

  def retrieve(itemId: QiitaItemId)(implicit session: DBSession = AutoSession): Option[RawPropsArticleJson] = {
    val id = itemId.value

    sql"""
          SELECT raw_json FROM raw_qiita_props_article_jsons
          WHERE item_id = $id;
       """
      .map(toRawJson)
      .single()
      .apply()
  }

  private def toRawJson(rs: WrappedResultSet): RawPropsArticleJson = {
    RawPropsArticleJson(rs.string("raw_json"))
  }
}
