<template>
  <div class="com-container">
    <el-aside class="left-warpper">
      <el-tree
        :data="leftTagTree"
        check-strictly
        node-key="id"
        default-expand-all
        @node-click="handleNodeClick"
      />
    </el-aside>
    <el-main class="right-warpper">
      <el-steps :active="active" finish-status="success">
        <el-step title="定义条件" />
        <el-step title="填写基本信息" />
        <el-step title="确认信息" />
      </el-steps>
      <el-tabs v-model="activeName" class="merge-tag-content">
        <el-tab-pane name="0" class="condition-tag">
          <el-row>
            <p>基本标签</p>
            <el-checkbox v-for="item in ruleTags" :key="item.id" v-model="item.checked" @change="handleRuleCheck(item,$event)">{{ item.name }}</el-checkbox>
          </el-row>
          <div class="choosed-tag">
            <div class="chooed-header">
              <p>已选条件</p>
              <a href="javascript:void(0)" @click="clearAll">清空</a>
            </div>
            <div v-for="(tag,index) in mergeTag.tags" :key="tag.id" class="choosed-tag-item">
              <span class="indicator">{{ index+1 }}</span>
              <el-input v-model="tag.name" class="tag-label" size="mini" readonly>
                <el-button slot="append" icon="el-icon-delete" @click="handleDelete(tag)" />
              </el-input>
              <el-select v-model="tag.condition" size="mini">
                <el-option
                  v-for="c in conditions"
                  :key="c.value"
                  :label="c.label"
                  :value="c.value"
                />
              </el-select>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="1" class="basic-info">填写基本信息</el-tab-pane>
        <el-tab-pane name="2" class="review">确认信息</el-tab-pane>
      </el-tabs>
      <el-button type="primary" style="position: absolute;bottom: 10px; right: 50%;" @click="next">下一步</el-button>
      <el-button style="position: absolute;bottom: 10px; right: 40%;" @click="cancel">取消</el-button>
    </el-main>
  </div>
</template>

<script>
import { listBasicTagTree, childTags } from '@/api/dw/profile'

export default {
  data() {
    return {
      leftTagTree: [],
      active: 0,
      activeName: '0',
      ruleTags: [],
      conditions: [
        {
          value: 'AND',
          label: '且'
        },
        {
          value: 'OR',
          label: '或'
        }
      ],
      conditionTitle: '',
      mergeTag: {
        tags: [],
        name: '',
        condition: '',
        intro: '',
        purpose: '',
        remark: ''
      }
    }
  },
  watch: {
    active: function(val) {
      this.activeName = val + ''
    }
  },
  created() {
    this.loadLeftTree()
  },
  methods: {
    loadLeftTree() {
      listBasicTagTree().then(response => {
        const { data: ret } = response.data
        this.leftTagTree = ret
      })
    },
    handleNodeClick(data, node) {
      if (node.level === 4) {
        this.conditionTitle = data.label
        childTags({ pid: data.id }).then(response => {
          const { data: ret } = response.data
          this.ruleTags = ret
        })
      } else {
        this.$notify.error({
          title: '提示',
          message: '请选择模型标签!'
        })
      }
    },
    next() {
      if (this.active++ >= 2) this.active = 0
    },
    cancel() {
      this.mergeTag = {
        tags: [],
        name: '',
        condition: '',
        intro: '',
        purpose: '',
        remark: ''
      }
      this.ruleTags = []
      this.$emit('create-cancel')
    },
    clearAll() {
      this.mergeTag.tags = []
      this.ruleTags.forEach(item => (item.checked = false))
    },
    handleRuleCheck(tag, event) {
      const index = this.mergeTag.tags.findIndex(item => item.id === tag.id)
      if (event) {
        if (index === -1) {
          const choosedTag = {
            id: tag.id,
            name: this.conditionTitle + '\t' + tag.name,
            condition: 'AND'
          }
          this.mergeTag.tags.push(choosedTag)
        }
      } else {
        this.mergeTag.tags.splice(index, 1)
      }
    },
    handleDelete(tag) {
      const index = this.mergeTag.tags.findIndex(item => item.id === tag.id)
      this.mergeTag.tags.splice(index, 1)
      // 移除选中
      this.ruleTags.forEach(item => {
        if (item.id === tag.id) {
          item.checked = false
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.com-container {
  width: 100%;
  height: 100%; // 和屏幕高度一致
  min-height: 400px;
  display: flex;
  .left-warpper {
    width: 200px !important;
    padding: 0px !important;
    height: 100%;
  }
  .right-warpper {
    .merge-tag-content {
      .condition-tag {
        .choosed-tag {
          margin-top: 5px;
          .chooed-header {
            height: 30px;
            line-height: 30px;
            display: flex;
            justify-content: space-between;
            p {
              margin: 0px;
            }
            a {
              display: block;
              color: white;
              padding: 0 10px;
              margin-right: 5px;
              background-color: #409eff;
            }
          }
          .choosed-tag-item {
            width: 45%;
            display: inline-flex;
            line-height: 28px;
            margin-top: 10px;
            margin-right: 30px;
            .indicator {
              display: block;
              box-sizing: content-box;
              text-align: center;
              background: cornflowerblue;
              width: 72px;
              position: relative;
              z-index: 999;

              &:after {
                border-left: 8px solid cornflowerblue;
                border-bottom: 8px solid transparent;
                border-top: 8px solid transparent;
                border-right: 8px solid transparent;
                content: '';
                position: absolute;
                right: -16px;
                top: 20%;
              }
            }
            .tag-label {
              margin-right: 5px;
              margin-left: -2px;
            }
          }
        }
      }
    }
  }
}
</style>
