<template>
  <el-container class="app-container">
    <el-aside class="left-warpper">
      <el-tree
        class="primary-tag-tree"
        :data="leftTagTree"
        check-strictly
        node-key="id"
        :default-expanded-keys="[1]"
        @node-click="handleNodeClick"
      />
    </el-aside>
    <el-main class="right-warpper">
      <div class="nav">
        <el-breadcrumb class="nav-breadcrumb" separator-class="el-icon-arrow-right">
          <el-breadcrumb-item v-for="item in leftTagStack" :key="item.id">{{ item.label }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="nav-operation">
          <div class="nav-search">
            <el-input v-model="searchTagName" placeholder="请输入关键词检索">
              <el-button slot="append" icon="el-icon-search" @click="handleSearchTag" />
            </el-input>
          </div>
          <el-button v-show="leftClickLevel===3" class="nav-addTag" type="primary" icon="el-icon-plus" size="medium" @click="modelTagDialog = true">新建业务标签</el-button>
          <el-button v-show="leftClickLevel!==3" type="primary" icon="el-icon-plus" size="medium" @click="primaryTagdialogVisible = true">添加主分类标签</el-button>
        </div>

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

        <el-dialog
          title="添加业务标签"
          :visible.sync="modelTagDialog"
          width="40%"
        >
          <el-form ref="modelForm" :model="modelTag" status-icon :rules="rules" size="small" label-width="100px">
            <el-form-item label="标签名称" prop="name">
              <el-input v-model="modelTag.name" type="text" />
            </el-form-item>
            <el-form-item label="标签分类" prop="industry">
              <div style="display:flex">
                <el-input v-model="modelTag.oneLevel" readonly type="text" />
                <el-input v-model="modelTag.towLevel" readonly style="margin:0 5px" type="text" />
                <el-input v-model="modelTag.threeLevel" readonly type="text" />
              </div>
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
                value-format="yyyy-MM-dd HH:mm"
                format="yyyy-MM-dd HH:mm"
              />
            </el-form-item>
            <el-form-item label="业务含义" prop="business">
              <el-input v-model="modelTag.business" type="textarea" :rows="2" placeholder="最多可以输入400个字符" />
            </el-form-item>
            <el-form-item label="标签规则" prop="rule">
              <el-input v-model="modelTag.rule" type="textarea" :rows="4" placeholder="key=value,例如：type=hive" />
            </el-form-item>
            <el-form-item label="程序入口" prop="modelMain">
              <el-input v-model="modelTag.modelMain" type="text" />
            </el-form-item>
            <el-form-item label="算法名称" prop="modelName">
              <el-input v-model="modelTag.modelName" type="text" />
            </el-form-item>
            <el-form-item label="算法引擎" prop="modelJar">
              <el-input v-model="modelTag.modelJar" type="text" :disabled="true">
                <el-upload
                  slot="append"
                  class="upload-demo"
                  action="https://jsonplaceholder.typicode.com/posts/"
                  :multiple="false"
                  :show-file-list="false"
                  :on-success="handleUploadSuccess"
                >
                  <el-button size="small" type="primary">点击上传</el-button>
                </el-upload>
              </el-input>
            </el-form-item>
            <el-form-item label="模型参数" prop="modelArgs">
              <el-input v-model="modelTag.modelArgs" type="textarea" :rows="2" placeholder="最多可以输入1000个字符" />
            </el-form-item>
            <el-form-item label-width="40%">
              <el-button type="primary" @click="submitmodelForm()">提交</el-button>
              <el-button @click="resetForm('modelForm')">重置</el-button>
            </el-form-item>
          </el-form>
        </el-dialog>
      </div>
      <hr style="width: 100%; height: 2px; border: none; background-color: #74bcff">
    </el-main>
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
        ],
        starEnd: [
          { required: true, message: '请输入更新周期', trigger: 'blur' }
        ],
        business: [
          { required: true, message: '请输入业务含义', trigger: 'blur' }
        ],
        rule: [{ required: true, message: '请输入标签规则', trigger: 'blur' }],
        modelMain: [
          { required: true, message: '请输入程序入口', trigger: 'blur' }
        ],
        modelName: [
          { required: true, message: '请输入算法名称', trigger: 'blur' }
        ],
        modelJar: [
          { required: true, message: '请输入算法引擎', trigger: 'blur' }
        ]
      },
      leftTagTree: [],
      selectProps: {
        value: 'id',
        checkStrictly: true,
        emitPath: false,
        expandTrigger: 'hover'
      },
      oneLevelTree: [], // 主分类添加form
      leftTagStack: [], // 面包屑
      leftClickLevel: 1, // 当前点击节点的层级
      searchTagName: undefined,
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
      modelTag: {
        name: '',
        schedule: 1,
        starEnd: '',
        business: '',
        rule: '',
        modelMain: '',
        modelName: '',
        modelJar: '',
        modelArgs: '',
        pid: '',
        oneLevel: '',
        towLevel: '',
        threeLevel: ''
      }
    }
  },
  created() {
    this.loadLeftTree()
    this.loadOneLevelTree()
  },
  methods: {
    handleNodeClick(data, node) {
      this.leftClickLevel = node.level
      // 1.解析节点链
      const stack = []
      stack.push(node.data)
      var parent = node.parent
      while (parent !== null) {
        stack.push(parent.data)
        parent = parent.parent
      }
      // 2.移除最后一个(重复的最顶层)
      stack.pop()
      // 3. 倒序
      stack.reverse()
      this.leftTagStack = stack
      this.modelTag.oneLevel = stack[0] !== undefined ? stack[0].label : ''
      this.modelTag.towLevel = stack[1] !== undefined ? stack[1].label : ''
      this.modelTag.threeLevel = stack[2] !== undefined ? stack[2].label : ''
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
          this.loadLeftTree()
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
    },
    handleSearchTag() {
      console.info(this.searchTagName)
    },
    handleUploadSuccess(response, file, fileList) {
      console.info(file)
    },
    submitmodelForm() {
      console.info(this.modelTag)
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.app-container {
  width: 100%;
  height: 100vh; // 和屏幕高度一致
  padding: 2px;

  .left-warpper {
    width: 200px !important;
    padding: 2px !important;
    display: flex;
    flex-direction: column;

    .primary-tag-tree {
      flex-grow: 1;
    }
  }

  .right-warpper {
    .nav {
      height: 40px;
      .nav-breadcrumb {
        float: left;
        line-height: 40px;
      }
      .nav-operation {
        float: right;
        display: flex;
        .nav-addTag {
          width: 150px;
        }
        .nav-search {
          width: 200px;
          line-height: 40px;
          margin-right: 10px;
        }
      }
    }
  }
}
</style>
