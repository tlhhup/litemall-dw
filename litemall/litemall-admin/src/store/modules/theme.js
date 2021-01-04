const theme = {
  state: {
    theme: 'chalk'
  },
  mutations: {
    changeTheme(state) {
      if (state.theme === 'chalk') {
        state.theme = 'macarons'
      } else {
        state.theme = 'chalk'
      }
    }
  }
}

export default theme
