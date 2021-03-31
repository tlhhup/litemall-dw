<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.modelName" clearable class="filter-item" style="width: 200px;" placeholder="请输入模型名称" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column align="center" label="模型名称" prop="modelName" />
      <el-table-column align="center" label="标签名称" prop="name" />
      <el-table-column align="center" label="业务描述" prop="business" />
      <el-table-column align="center" label="调度周期" prop="scheduleRule" />
      <el-table-column align="center" label="标签状态" prop="state">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.state.dec }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="150" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-show="scope.row.state.state===1" type="primary" size="mini" @click="handleDetail(scope.row)">模型审核</el-button>
          <el-button v-show="[3,6].includes(scope.row.state.state)" type="primary" size="mini" @click="handleOnline(scope.row)">模型上线</el-button>
          <el-button v-show="scope.row.state.state===8" type="primary" size="mini" @click="handleOffline(scope.row)">模型下线</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 审核 -->
    <el-dialog
      title="模型详情"
      :visible.sync="modelTagDialog"
      width="40%"
    >
      <el-form :model="modelTag" status-icon size="small" label-width="100px">
        <el-form-item label="算法名称" prop="modelName">
          <el-input v-model="modelTag.modelName" type="text" />
        </el-form-item>
        <el-form-item label="更新周期" prop="starEnd">
          <el-select v-model="modelTag.schedule" placeholder="请选择" style="width:120px;margin-right: 8px;">
            <el-option
              v-for="item in modelScheduleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-date-picker
            v-model="modelTag.starEnd"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-ddTHH:mm+0800"
            format="yyyy-MM-dd HH:mm"
          />
        </el-form-item>
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="modelTag.name" type="text" />
        </el-form-item>
        <el-form-item label="业务含义" prop="business">
          <el-input v-model="modelTag.business" type="textarea" :rows="2" placeholder="最多可以输入400个字符" />
        </el-form-item>
        <el-form-item label="标签规则" prop="rule">
          <el-input v-model="modelTag.rule" type="textarea" :rows="3" placeholder="key=value,例如：type=hive" />
        </el-form-item>
        <el-form-item label="程序入口" prop="modelMain">
          <el-input v-model="modelTag.modelMain" type="text" />
        </el-form-item>
        <el-form-item label="算法引擎" prop="modelPath">
          <el-input v-model="modelTag.modelPath" type="text" :disabled="true" />
        </el-form-item>
        <el-form-item label="模型参数" prop="modelArgs">
          <el-input v-model="modelTag.modelArgs" type="textarea" :rows="2" placeholder="最多可以输入1000个字符" />
        </el-form-item>
        <el-form-item label="Spark参数" prop="sparkOpts">
          <el-input v-model="modelTag.sparkOpts" type="textarea" :rows="2" placeholder="最多可以输入1000个字符" />
        </el-form-item>
        <el-form-item label-width="40%">
          <el-button type="primary" @click="approve">通过</el-button>
          <el-button @click="reject">不通过</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 模型发布 -->
    <el-dialog
      title="模型发布"
      :visible.sync="modelPublishDialog"
      width="40%"
    >
      <el-form :model="modelTag" status-icon size="small" label-width="100px">
        <el-form-item label="算法名称" prop="modelName">
          <el-input v-model="modelTag.modelName" type="text" readonly />
        </el-form-item>
        <el-form-item label="更新周期" prop="starEnd">
          <el-select v-model="modelTag.schedule" placeholder="请选择" disabled style="width:120px;margin-right: 8px;">
            <el-option
              v-for="item in modelScheduleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-date-picker
            v-model="modelTag.starEnd"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-ddTHH:mm+0800"
            format="yyyy-MM-dd HH:mm"
            readonly
          />
        </el-form-item>
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="modelTag.name" type="text" readonly />
        </el-form-item>
        <el-form-item label="业务含义" prop="business">
          <el-input v-model="modelTag.business" type="textarea" :rows="2" readonly />
        </el-form-item>
        <el-form-item label="标签规则" prop="rule">
          <el-input v-model="modelTag.rule" type="textarea" :rows="3" placeholder="key=value,例如：type=hive" readonly />
        </el-form-item>
        <el-form-item label="程序入口" prop="modelMain">
          <el-input v-model="modelTag.modelMain" type="text" readonly />
        </el-form-item>
        <el-form-item label="算法引擎" prop="modelPath">
          <el-input v-model="modelTag.modelPath" type="text" readonly />
        </el-form-item>
        <el-form-item label="模型参数" prop="modelArgs">
          <el-input v-model="modelTag.modelArgs" type="textarea" :rows="2" readonly />
        </el-form-item>
        <el-form-item label="Spark参数" prop="sparkOpts">
          <el-input v-model="modelTag.sparkOpts" type="textarea" :rows="2" readonly />
        </el-form-item>
        <el-form-item label-width="40%">
          <el-button type="primary" @click="publishModel">发布</el-button>
          <el-button @click="modelPublishDialog=false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
  </div>
</template>

<script>
import {
  listSubmitModel,
  approveModel,
  publishModel,
  offlineModel
} from '@/api/dw/profile'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        modelName: undefined
      },
      modelTagDialog: false,
      modelScheduleOptions: [
        {
          value: 1,
          label: '每天'
        },
        {
          value: 2,
          label: '每周'
        },
        {
          value: 3,
          label: '每月'
        },
        {
          value: 4,
          label: '每年'
        }
      ],
      modelTag: {},
      approveForm: {},
      modelPublishDialog: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      listSubmitModel(this.listQuery).then(response => {
        const { data: ret } = response.data
        this.list = ret
        this.total = ret.length
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listLoading = true
      this.getList()
    },
    handleDetail(data) {
      Object.assign(this.modelTag, data)
      this.modelTagDialog = true
    },
    approve() {
      this.approveForm = {
        tagId: this.modelTag.id,
        modelId: this.modelTag.modelId,
        state: 2
      }
      approveModel(this.approveForm).then(response => {
        const { data: ret } = response.data
        this.modelTagDialog = false
        if (!ret) {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        }
      })
    },
    reject() {
      this.approveForm = {
        tagId: this.modelTag.id,
        modelId: this.modelTag.modelId,
        state: 7
      }
      approveModel(this.approveForm).then(response => {
        const { data: ret } = response.data
        this.modelTagDialog = false
        if (!ret) {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        }
      })
    },
    handleOnline(data) {
      Object.assign(this.modelTag, data)
      this.modelPublishDialog = true
    },
    publishModel() {
      const data = {
        tagId: this.modelTag.id,
        modelId: this.modelTag.modelId
      }
      publishModel(data).then(response => {
        const { data: ret } = response.data
        this.modelPublishDialog = false
        if (!ret) {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        }
      })
    },
    handleOffline(data) {
      const params = {
        tagId: data.id,
        modelId: data.modelId
      }
      offlineModel(params).then(response => {
        const { data: ret } = response.data
        this.modelTagDialog = false
        if (!ret) {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
</style>
