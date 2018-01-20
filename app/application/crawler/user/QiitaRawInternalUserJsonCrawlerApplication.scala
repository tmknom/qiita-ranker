package application.crawler.user

import javax.inject.{Inject, Singleton}

import domain.crawler.{Progress, Sleeper}
import domain.qiita.user._
import library.scalaj.ScalajHttpException
import play.api.Logger

import scala.collection.mutable

@Singleton
final class QiitaRawInternalUserJsonCrawlerApplication @Inject()(
    gateway:                 QiitaUserInternalApiGateway,
    repository:              QiitaRawInternalUserJsonRepository,
    qiitaUserNameRepository: QiitaUserNameRepository
) {
  @SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
  private val errorQiitaUserNames = mutable.ListBuffer.empty[String]

  def crawl(): Unit = {
    val qiitaUserNames = qiitaUserNameRepository.retrieveRecently()
    val itemsCount     = qiitaUserNames.size
    qiitaUserNames.zipWithIndex.foreach {
      case (qiitaUserName, index) =>
        val progress = Progress.calculate(index, itemsCount)
        quietlyCrawlOneUser(qiitaUserName, progress)
    }
    Logger.warn(s"${this.getClass.getSimpleName} crawl error ${errorQiitaUserNames.size.toString} users : ${errorQiitaUserNames.toString()}")
  }

  private def quietlyCrawlOneUser(qiitaUserName: QiitaUserName, progress: String): Unit = {
    try {
      val rawInternalUserJson = gateway.fetch(qiitaUserName)
      val crawledDateTime     = CrawledDateTime.now()
      repository.register(qiitaUserName, rawInternalUserJson, crawledDateTime)
      Logger.info(s"${this.getClass.getSimpleName} crawled ${qiitaUserName.value} $progress")
    } catch {
      // 処理を止めてほしくないのでログ吐いて握りつぶす
      case e: ScalajHttpException => {
        errorQiitaUserNames += qiitaUserName.value
        Logger.warn(s"${this.getClass.getSimpleName} crawl error ${qiitaUserName.value} $progress because ${e.message}")
      }
    } finally {
      Sleeper.sleep()
    }
  }
}
