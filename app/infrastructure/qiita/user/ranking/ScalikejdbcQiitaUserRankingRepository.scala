package infrastructure.qiita.user.ranking

import javax.inject.Singleton

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import domain.qiita.user.contribution.Contribution
import domain.qiita.user.ranking.{QiitaUserRanking, QiitaUserRankingRepository}
import domain.qiita.user.{QiitaUserId, QiitaUserName}
import play.api.Logger
import scalikejdbc._

@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.DefaultArguments", "org.wartremover.warts.Nothing"))
@Singleton
final class ScalikejdbcQiitaUserRankingRepository extends QiitaUserRankingRepository {
  override def register(qiitaUserRanking: QiitaUserRanking)(implicit session: DBSession = AutoSession): Unit = {
    val userName     = qiitaUserRanking.name.value
    val contribution = qiitaUserRanking.contribution.value
    try {
      sql"""
            INSERT INTO qiita_user_rankings (user_name, contribution)
            VALUES ($userName, $contribution);
         """
        .update()
        .apply()
    } catch {
      // 処理を止めてほしくないのでログ吐いて握りつぶす
      case e: MySQLIntegrityConstraintViolationException => Logger.warn(s"insert error $userName, because ${e.getMessage}")
    }

    () // 明示的に Unit を返す
  }

  def retrieveAll()(implicit session: DBSession = AutoSession): Seq[QiitaUserRanking] = {
    sql"""
          SELECT qu.user_name, qur.contribution
          FROM qiita_user_rankings AS qur
          INNER JOIN qiita_users AS qu
          ON qur.user_name = qu.user_name
          ORDER BY qur.contribution DESC;
       """
      .map(toQiitaUserRanking)
      .list()
      .apply()
  }

  private def toQiitaUserRanking(rs: WrappedResultSet): QiitaUserRanking = {
    QiitaUserRanking(
      QiitaUserName(rs.string("user_name")),
      Contribution(rs.int("contribution"))
    )
  }
}
