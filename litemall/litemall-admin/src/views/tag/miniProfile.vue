<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户名" />
      <el-input v-model="listQuery.mobile" clearable class="filter-item" style="width: 200px;" placeholder="请输入手机号" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
    </div>

    <!-- 用户选择对话框 -->
    <el-dialog
      title="请选择用户"
      :visible.sync="userChooseDialog"
      width="60%"
      :before-close="handleClose"
    >
      <!-- 用户列表 -->
      <el-table
        v-loading="listLoading"
        :data="list"
        element-loading-text="正在查询中。。。"
        border
        fit
        highlight-current-row
        @current-change="handleCurrentChange"
      >

        <el-table-column align="center" width="100px" label="用户ID" prop="id" sortable />

        <el-table-column align="center" label="用户名" prop="username" />

        <el-table-column align="center" label="手机号码" prop="mobile" />

        <el-table-column align="center" label="性别" prop="gender">
          <template slot-scope="scope">
            <el-tag>{{ genderDic[scope.row.gender] }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" label="生日" prop="birthday" />

        <el-table-column align="center" label="用户等级" prop="userLevel">
          <template slot-scope="scope">
            <el-tag>{{ levelDic[scope.row.userLevel] }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" label="状态" prop="status">
          <template slot-scope="scope">
            <el-tag>{{ statusDic[scope.row.status] }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="handleFilter" />

      <span slot="footer" class="dialog-footer">
        <el-button @click="userChooseDialog = false">取 消</el-button>
        <el-button type="primary" @click="handleUserTag">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 用户信息 -->
    <div class="user-container">
      <div style="margin:0 auto">
        {{ userDesc }}
      </div>
    </div>

    <!-- 画像关系图 -->
    <TagView :tags="tags" />
  </div>
</template>

<script>
import { searchUserTag } from '@/api/dw/profile'
import { fetchList } from '@/api/user'
import TagView from './components/TagView'
import Pagination from '@/components/Pagination'

export default {
  components: { TagView, Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 6,
        username: undefined,
        mobile: undefined
      },
      tags: undefined,
      userChooseDialog: false,
      genderDic: ['未知', '男', '女'],
      levelDic: ['普通用户', 'VIP用户', '高级VIP用户'],
      statusDic: ['可用', '禁用', '注销'],
      user: null,
      userDesc: ''
    }
  },
  watch: {
    user: function(val) {
      if (val) {
        this.userDesc = '用户名：' + val.username + ' 电话：' + val.mobile
      }
    }
  },
  methods: {
    handleFilter() {
      // 检查数据合法性
      if (!(this.listQuery.username != null || this.listQuery.mobile != null)) {
        this.$notify.info({
          title: '提示',
          message: '请输入手机号或用户名!'
        })

        return
      }
      // 切换状态
      this.listLoading = true
      this.userChooseDialog = true
      // 加载数据
      fetchList(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    handleCurrentChange(row) {
      this.user = row
    },
    handleUserTag() {
      this.cleanQuery()
      this.userChooseDialog = false
      if (this.user == null || this.user.id == null) {
        return
      }
      const param = { userId: this.user.id }
      searchUserTag(param).then(response => {
        const { data: ret } = response.data
        this.tags = ret
      })
    },
    handleClose(done) {
      this.cleanQuery()
      done()
    },
    cleanQuery() {
      this.listQuery = {
        username: undefined,
        mobile: undefined
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
</style>
