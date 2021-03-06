import Vue from 'vue'
import Vuex from 'vuex'
import ArticleListStore from './modules/ArticleListStore'
import UserContributionStore from './modules/UserContributionStore'
import UserHatenaStore from './modules/UserHatenaStore'
import UserTotalStore from './modules/UserTotalStore'
import UserStore from "./modules/UserStore"

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    ArticleListStore: ArticleListStore,
    UserContributionStore: UserContributionStore,
    UserHatenaStore: UserHatenaStore,
    UserTotalStore: UserTotalStore,
    UserStore: UserStore,
  },
  // https://vuex.vuejs.org/ja/strict.html
  strict: process.env.NODE_ENV !== 'production'
});
