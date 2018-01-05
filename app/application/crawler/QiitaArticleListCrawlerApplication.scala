package application.crawler

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}

import domain.qiita.article.page.QiitaArticlePage
import domain.qiita.article.{QiitaArticleListGateway, QiitaArticleRepository}
import play.api.Logger

@Singleton
final class QiitaArticleListCrawlerApplication @Inject()(
    gateway:    QiitaArticleListGateway,
    repository: QiitaArticleRepository
) {
  private val SleepTimeMilliseconds = 100.toLong

  def crawl(): Unit = {
    QiitaArticlePage.range.foreach { currentPage =>
      val articles = gateway.fetch(currentPage)
      articles.foreach { article =>
        repository.register(article)
      }

      Logger.info(s"crawled $currentPage / ${QiitaArticlePage.PageMax}")
      TimeUnit.MILLISECONDS.sleep(SleepTimeMilliseconds)
    }
  }

}
