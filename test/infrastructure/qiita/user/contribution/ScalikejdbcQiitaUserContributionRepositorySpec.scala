package infrastructure.qiita.user.contribution

import domain.qiita.user.{CrawledDateTime, QiitaUserName}
import domain.qiita.user.contribution.{ArticlesCount, QiitaUserContribution, QiitaUserContributionCrawledEvent}
import fixture.db.qiita.QiitaUsersFixture
import library.datetime.DateTimeProvider
import library.test.db.{DatabaseFixture, DatabaseSpec}

// scalastyle:off magic.number
class ScalikejdbcQiitaUserContributionRepositorySpec extends DatabaseSpec {
  val sut = new ScalikejdbcQiitaUserContributionRepository()

  "#register" should {
    "登録できること" in { implicit session =>
      DatabaseFixture.setup(QiitaUsersFixture.Default.Fixtures)

      val crawledEvent = QiitaUserContributionCrawledEvent(
        qiitaUserName         = QiitaUserName(QiitaUsersFixture.Default.UserName),
        qiitaUserContribution = QiitaUserContribution(1234),
        articlesCount         = ArticlesCount(123),
        crawledDateTime       = CrawledDateTime(DateTimeProvider.nowJST())
      )

      sut.register(crawledEvent)
    }
  }
}
