<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-date-picker
        v-model="listQuery.date"
        value-format="yyyy-MM-dd"
        :picker-options="pickerOptions"
        clearable
        class="filter-item"
        style="width: 200px;"
        placeholder="请选择查询日期"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="日期" prop="dt" sortable />

      <el-table-column align="center" label="商品SKU_ID" prop="skuId" />

      <el-table-column align="center" label="差评率" prop="appraiseBadRatio" sortable />

      <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
  </div>
</template>

<script>
import { listAppraiseBadTopn } from '@/api/dw/report'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'AppraiseBad',
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        date: undefined,
        sort: 'appraise_bad_ratio',
        order: 'desc'
      },
      downloadLoading: false,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listAppraiseBadTopn(this.listQuery).then(response => {
        this.list = response.data.data.records
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['日期', '商品SKU_ID', '差评率']
        const filterVal = ['dt', 'skuId', 'appraiseBadRatio']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '商品差评信息')
        this.downloadLoading = false
      })
    },
    handleDetail(row) {
    }
  }
}
</script>
