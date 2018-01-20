package application.crawler.article

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}

import domain.crawler.Progress
import domain.qiita.article._
import domain.qiita.article.contribution._
import domain.qiita.article.json.RawPropsArticleJson
import play.api.Logger
import scalikejdbc.DB

import scala.collection.mutable

@Singleton
final class QiitaArticleContributionCrawlerApplication @Inject()(
    hatenaGateway:          HatenaGateway,
    facebookGateway:        FacebookGateway,
    pocketGateway:          PocketGateway,
    repository:             QiitaArticleContributionRepository,
    historyRepository:      QiitaArticleContributionHistoryRepository,
    qiitaArticleRepository: QiitaArticleRepository,
    idRepository:           QiitaArticleIdRepository,
    rawJsonRepository:      QiitaRawPropsArticleJsonRepository
) {
  @SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
  private val errors = mutable.ListBuffer.empty[String]

  private val SleepTimeMilliseconds = 100.toLong

  def crawl(): Unit = {
    val items      = idRepository.retrieveNotRegisteredContribution()
    val itemsCount = items.size
    items.zipWithIndex.foreach {
      case (qiitaItemId, index) =>
        val progress = Progress.calculate(index, itemsCount)
        quietlyCrawl(qiitaItemId, progress)
    }
    Logger.warn(s"${this.getClass.getSimpleName} crawl error ${errors.size.toString} items : ${errors.toString()}")
  }

  private def quietlyCrawl(qiitaItemId: QiitaItemId, progress: String): Unit = {
    try {
      val event = crawlContribution(qiitaItemId)
      registerWithTransaction(event)
      Logger.info(s"${this.getClass.getSimpleName} crawled ${qiitaItemId.value} $progress")
    } catch {
      case e: Exception =>
        errors += qiitaItemId.value
        Logger.warn(s"${this.getClass.getSimpleName} crawl error ${qiitaItemId.value}.", e)
    } finally {
      TimeUnit.MILLISECONDS.sleep(SleepTimeMilliseconds)
    }
  }

  private def registerWithTransaction(event: QiitaArticleContributionCrawledEvent): Unit = {
    DB.localTx { implicit session =>
      repository.register(event)
      historyRepository.register(event)
    }
  }

  private def crawlContribution(qiitaItemId: QiitaItemId): QiitaArticleContributionCrawledEvent = {
    val (hatenaCount, facebookCount, pocketCount) = crawlSocial(qiitaItemId)

    val rawArticleJson = getRawArticleJson(qiitaItemId)

    val contribution = QiitaArticleContribution(
      rawArticleJson.toLikesCount,
      rawArticleJson.toCommentsCount,
      hatenaCount,
      facebookCount,
      pocketCount
    )

    contribution.toCrawledEvent(qiitaItemId)
  }

  private def crawlSocial(qiitaItemId: QiitaItemId): (HatenaCount, FacebookCount, PocketCount) = {
    val qiitaArticle    = qiitaArticleRepository.retrieve(qiitaItemId)
    val qiitaArticleUrl = qiitaArticle.url

    val hatenaCount   = hatenaGateway.fetch(qiitaArticleUrl)
    val facebookCount = facebookGateway.fetch(qiitaArticleUrl)
    val pocketCount   = pocketGateway.fetch(qiitaArticleUrl)

    (hatenaCount, facebookCount, pocketCount)
  }

  private def getRawArticleJson(qiitaItemId: QiitaItemId): RawPropsArticleJson = {
    val optionValue = rawJsonRepository.retrieve(qiitaItemId)
    optionValue match {
      case Some(v) => v
      case None    => throw new RuntimeException(s"ココに来たらバグなので雑に例外をスロー ${qiitaItemId.value}")
    }
  }
}
