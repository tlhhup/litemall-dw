<template>
  <div class="goods-comment">
    <van-list
      v-model="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="getComments">
      <Comment v-model="comments" @item-click="itemClick"/>
    </van-list>
  </div>
</template>

<script>
import { commentList } from '@/api/api'
import Comment from '@/components/comment'
import { List } from 'vant'

export default {
  components: {
    Comment,
    [List.name]: List
  },
  props: {
    itemId: [String, Number]
  },
  data() {
    return {
      comments: [],
      query: {
        type: 0,
        showType: 1,
        page: 1,
        valueId: this.itemId
      },
      loading: false,
      finished: false
    }
  },
  created() {
    this.initData()
  },
  methods: {
    initData() {
      this.query.page = 0
      this.comments = []
      // this.getComments()
    },
    getComments() {
      this.query.page++
      commentList(this.query).then(response => {
        this.comments.push(...response.data.data.list)
        this.loading = false
        this.finished = response.data.data.page >= response.data.data.pages
      })
    },
    itemClick(item) {
      console.info(item)
    }
  }
}
</script>

<style>
</style>
