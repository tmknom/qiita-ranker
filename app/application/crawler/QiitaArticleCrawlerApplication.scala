package application.crawler

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}

import domain.qiita.article.{QiitaArticleGateway, QiitaArticleIdRepository, QiitaArticleRepository, QiitaItemId}
import play.api.Logger

@Singleton
final class QiitaArticleCrawlerApplication @Inject()(
    gateway:                  QiitaArticleGateway,
    repository:               QiitaArticleRepository,
    qiitaArticleIdRepository: QiitaArticleIdRepository
) {
  private val SleepTimeMilliseconds = 100.toLong

  def crawl(): Unit = {
    val qiitaItemIds = qiitaArticleIdRepository.retrieveAll()
    qiitaItemIds.zipWithIndex.foreach {
      case (qiitaItemId, index) =>
        quietlyCrawl(qiitaItemId, index, qiitaItemIds.size)
        TimeUnit.MILLISECONDS.sleep(SleepTimeMilliseconds)
    }
  }

  private def quietlyCrawl(qiitaItemId: QiitaItemId, index: Int, qiitaItemIdsCount: Int): Unit = {
    try {
      val qiitaArticle = gateway.fetch(qiitaItemId)
      repository.register(qiitaArticle)
      log(qiitaItemId, index, qiitaItemIdsCount)
    } catch {
      case e: Exception =>
        Logger.warn(s"${this.getClass.getSimpleName} crawl error ${qiitaItemId.value}.", e)
    }
  }

  private def log(qiitaItemId: QiitaItemId, index: Int, qiitaItemIdsCount: Int) = {
    val progress = ((index + 1) / qiitaItemIdsCount) * 100.0
    Logger.info(s"${this.getClass.getSimpleName} crawled ${qiitaItemId.value} : ${index + 1} / $qiitaItemIdsCount ($progress%)")
  }
}
