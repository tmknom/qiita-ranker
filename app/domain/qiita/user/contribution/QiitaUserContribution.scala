package domain.qiita.user.contribution

import domain.qiita.article.contribution.HatenaCount

final case class QiitaUserContribution(
    contribution:  Contribution,
    articlesCount: ArticlesCount,
    hatenaCount:   HatenaCount
) {

  def totalEvaluation: TotalEvaluation = {
    TotalEvaluation(contribution.value + hatenaCount.value)
  }

  def contributionAverage: ContributionAverage = {
    articlesCount.contributionAverage(contribution)
  }

  def hatenaCountAverage: HatenaCountAverage = {
    articlesCount.hatenaCountAverage(hatenaCount)
  }
}
