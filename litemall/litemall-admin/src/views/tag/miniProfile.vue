<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户名" />
      <el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户Id" />
      <el-input v-model="listQuery.mobile" clearable class="filter-item" style="width: 200px;" placeholder="请输入手机号" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
    </div>

    <!-- 画像关系图 -->
    <TagView :tags="tags" />
  </div>
</template>

<script>
import { searchUserTag } from '@/api/dw/profile'
import TagView from './components/TagView'

export default {
  components: { TagView },
  data() {
    return {
      listQuery: {
        username: undefined,
        mobile: undefined,
        userId: undefined
      },
      tags: undefined
    }
  },
  methods: {
    handleFilter() {
      searchUserTag(this.listQuery).then(response => {
        const { data: ret } = response.data
        this.tags = ret
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
</style>
