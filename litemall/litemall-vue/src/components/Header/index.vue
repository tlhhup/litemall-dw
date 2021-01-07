<template>
  <div>
    <van-nav-bar v-show="showHeader" :title="title" left-text="返回" left-arrow @click-left="goBack"/>
  </div>

</template>
<script>
import { NavBar } from 'vant'
import { mapState } from 'vuex'

export default {
  name: 'VHeader',
  components: {
    [NavBar.name]: NavBar
  },
  data() {
    return {}
  },
  computed: {
    ...mapState({
      showHeader: state => state.showHeader,
      title: state => state.title
    })
  },
  methods: {
    goBack() {
      this.$router.back(-1)
    }
  }
}
</script>

