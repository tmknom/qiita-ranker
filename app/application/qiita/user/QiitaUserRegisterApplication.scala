package application.qiita.user

import javax.inject.{Inject, Singleton}

import domain.qiita.user.contribution.{QiitaUserContributionHistoryRepository, QiitaUserContributionRepository}
import domain.qiita.user.{QiitaRawInternalUserJsonRepository, QiitaUserName, QiitaUserProfileRepository, RawInternalUserJson}
import scalikejdbc.DB

@Singleton
final class QiitaUserRegisterApplication @Inject()(
    qiitaUserProfileRepository:             QiitaUserProfileRepository,
    qiitaUserContributionRepository:        QiitaUserContributionRepository,
    qiitaUserContributionHistoryRepository: QiitaUserContributionHistoryRepository,
    qiitaRawInternalUserJsonRepository:     QiitaRawInternalUserJsonRepository
) {

  def registerRecently(): Unit = {
    val qiitaUserNames = qiitaRawInternalUserJsonRepository.retrieveRecently()
    qiitaUserNames.foreach(register)
  }

  private def register(qiitaUserName: QiitaUserName): Unit = {
    val optionValue = qiitaRawInternalUserJsonRepository.retrieve(qiitaUserName)
    val rawInternalUserJson = optionValue match {
      case Some(v) => v
      case None    => throw new RuntimeException(s"ココに来たらバグなので雑に例外をスロー ${qiitaUserName.value}")
    }

    registerWithTransaction(rawInternalUserJson)
  }

  private def registerWithTransaction(rawInternalUserJson: RawInternalUserJson): Unit = {
    DB.localTx { implicit session =>
      qiitaUserProfileRepository.register(rawInternalUserJson.toQiitaUserProfile)

      val crawledEvent = rawInternalUserJson.toCrawledEvent
      qiitaUserContributionRepository.register(crawledEvent)
      qiitaUserContributionHistoryRepository.register(crawledEvent)
    }
  }
}