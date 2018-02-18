import api from '../../api/users/users'

export default {
  namespaced: true,

  state: {
    user: {}
  },

  mutations: {
    replace(state, user) {
      state.user = user
    }
  },

  actions: {
    fetchUser({commit}, payload) {
      api.request(payload.name).then((result) => {
        commit('replace', result)
      }).catch(function (error) {
        console.log(error);
      });
    }
  }
};
