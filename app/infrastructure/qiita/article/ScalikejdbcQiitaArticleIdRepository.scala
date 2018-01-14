package infrastructure.qiita.article

import javax.inject.Singleton

import domain.qiita.article.{QiitaArticleIdRepository, QiitaItemId}
import scalikejdbc._

@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.DefaultArguments", "org.wartremover.warts.Nothing"))
@Singleton
final class ScalikejdbcQiitaArticleIdRepository extends QiitaArticleIdRepository {
  override def register(qiitaItemId: QiitaItemId)(implicit session: DBSession = AutoSession): Unit = {
    val itemId = qiitaItemId.value

    sql"""
          INSERT INTO qiita_article_ids (item_id)
          VALUES ($itemId);
       """
      .update()
      .apply()

    () // 明示的に Unit を返す
  }

  override def retrieveRecently()(implicit session: DBSession = AutoSession): List[QiitaItemId] = {
    sql"""
          SELECT item_id FROM qiita_article_ids AS qai
          WHERE NOT EXISTS
          (SELECT 1 FROM raw_qiita_props_article_jsons AS r WHERE r.item_id = qai.item_id)
          ORDER BY qai.id ASC;
       """
      .map(toQiitaItemId)
      .list()
      .apply()
  }

  private def toQiitaItemId(rs: WrappedResultSet): QiitaItemId = {
    QiitaItemId(rs.string("item_id"))
  }
}
