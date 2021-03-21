<template>
  <el-container class="app-container">
    <el-aside class="left-warpper">
      <el-tree
        class="primary-tag-tree"
        :props="props"
        :load="loadNode"
        lazy
        @check-change="handleCheckChange"
      />
      <el-row class="primary-tag-bt">
        <el-button type="primary" icon="el-icon-edit" size="medium" @click="primaryTagdialogVisible = true">添加主分类标签</el-button>
      </el-row>

      <el-dialog
        title="添加主分类标签"
        :visible.sync="primaryTagdialogVisible"
        width="30%"
      >
        <el-form ref="ptForm" :model="ptForm" status-icon :rules="rules" label-width="100px">
          <el-form-item label="标签名称" prop="name">
            <el-input v-model="ptForm.name" type="text" />
          </el-form-item>
          <el-form-item label="所属行业" prop="industry">
            <el-input v-model="ptForm.industry" type="text" />
          </el-form-item>
          <el-form-item label="父级标签" prop="pid">
            <el-cascader v-model="ptForm.pid" :options="oneLevelTree" :props="selectProps" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitPrimaryTagForm()">提交</el-button>
            <el-button @click="resetForm('ptForm')">重置</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </el-aside>
    <el-main class="right-warpper">Main</el-main>
  </el-container>
</template>

<script>
import {
  listBasicTagTree,
  oneLevelTag,
  createPrimaryTag
} from '@/api/dw/profile'

export default {
  name: 'BasicTag',
  data() {
    return {
      primaryTagdialogVisible: false,
      ptForm: {
        name: '',
        industry: '',
        pid: ''
      },
      rules: {
        name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
        industry: [
          { required: true, message: '请输入所属行业', trigger: 'blur' }
        ]
      },
      props: {
        label: 'name',
        children: 'zones'
      },
      count: 1,
      leftTagTree: [],
      selectProps: {
        value: 'id',
        checkStrictly: true,
        emitPath: false,
        expandTrigger: 'hover'
      },
      oneLevelTree: []
    }
  },
  created() {
    this.loadLeftTree()
    this.loadOneLevelTree()
  },
  methods: {
    handleCheckChange(data, checked, indeterminate) {
      console.log(data, checked, indeterminate)
    },
    handleNodeClick(data) {
      console.log(data)
    },
    loadNode(node, resolve) {
      if (node.level === 0) {
        return resolve([{ name: 'region1' }, { name: 'region2' }])
      }
      if (node.level > 3) return resolve([])

      var hasChild
      if (node.data.name === 'region1') {
        hasChild = true
      } else if (node.data.name === 'region2') {
        hasChild = false
      } else {
        hasChild = Math.random() > 0.5
      }

      setTimeout(() => {
        var data
        if (hasChild) {
          data = [
            {
              name: 'zone' + this.count++
            },
            {
              name: 'zone' + this.count++
            }
          ]
        } else {
          data = []
        }

        resolve(data)
      }, 500)
    },
    loadOneLevelTree() {
      oneLevelTag().then(response => {
        const { data: ret } = response.data
        this.oneLevelTree = ret
      })
    },
    loadLeftTree() {
      listBasicTagTree().then(response => {
        const { data: ret } = response.data
        this.leftTagTree = ret
      })
    },
    submitPrimaryTagForm() {
      createPrimaryTag(this.ptForm).then(response => {
        const { data: ret } = response.data
        if (ret) {
          this.primaryTagdialogVisible = false
          this.ptForm = {
            name: '',
            industry: '',
            pid: ''
          }
        } else {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.app-container {
  width: 100%;
  height: 90vh; // 和屏幕高度一致
  padding: 2px;

  .left-warpper {
    width: 200px !important;
    padding: 2px !important;
    display: flex;
    flex-direction: column;

    .primary-tag-tree {
      flex-grow: 1;
    }

    .primary-tag-bt {
      margin-top: 10px;
      text-align: center;
    }
  }

  .right-warpper {
  }
}
</style>
