package application.crawler

import domain.qiita.user.contribution.{QiitaUserContribution, QiitaUserContributionGateway, QiitaUserContributionRepository}
import domain.qiita.user.{QiitaUser, QiitaUserId, QiitaUserName, QiitaUserRepository}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

// scalastyle:off magic.number
class QiitaUserContributionCrawlerApplicationSpec extends PlaySpec with MockitoSugar with BeforeAndAfter {

  private val mockQiitaUserRepository             = mock[QiitaUserRepository]
  private val mockQiitaUserContributionRepository = mock[QiitaUserContributionRepository]
  private val mockQiitaUserContributionGateway    = mock[QiitaUserContributionGateway]

  before {
    when(mockQiitaUserContributionGateway.fetch(any[QiitaUserName])).thenReturn(QiitaUserContribution(1234))
    when(mockQiitaUserContributionRepository.register(any[QiitaUserId], any[QiitaUserContribution])).thenReturn(1)
    when(mockQiitaUserRepository.retrieveAll()).thenReturn(Seq(QiitaUser(QiitaUserId(1), QiitaUserName("jojo"))))
  }

  "QiitaUserContributionCrawlerApplication#crawl" should {
    "クロールできること" in {
      val sut = new QiitaUserContributionCrawlerApplication(
        mockQiitaUserContributionGateway,
        mockQiitaUserContributionRepository,
        mockQiitaUserRepository
      )

      sut.crawl()
    }
  }
}