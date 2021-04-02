<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 160px;" placeholder="请输入标签名称" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit" @click="dialogVisible = true">添加</el-button>
    </div>
    <!-- 创建组合标签 -->
    <el-dialog
      title="创建组合标签"
      :visible.sync="dialogVisible"
      width="65%"
    >
      <CreateMergeTag
        :merge-tag-id="mergeTagId"
        @create-cancel="dialogVisible=false"
        @reload-list="handleReload"
      />
    </el-dialog>

    <!-- 列表 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="标签名称" prop="name" />
      <el-table-column align="center" min-width="100" label="标签条件" prop="condition" />
      <el-table-column align="center" label="标签含义" prop="intro" />
      <el-table-column align="center" label="组合用途" prop="purpose" />
      <el-table-column align="center" label="标签状态" prop="state" />
      <el-table-column align="center" label="备注" prop="remark" />
      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import CreateMergeTag from './components/CreateMergeTag'
import Pagination from '@/components/Pagination'

export default {
  components: { CreateMergeTag, Pagination },
  data() {
    return {
      dialogVisible: false,
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: ''
      },
      mergeTagId: undefined
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = false
    },
    handleFilter() {},
    handleUpdate(row) {},
    handleDelete(row) {},
    handleReload() {
      this.dialogVisible = false
      this.getList()
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.app-container {
  width: 100%;
  height: 100vh; // 和屏幕高度一致
}
</style>
