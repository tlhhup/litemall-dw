<template>
  <div class="com-container">
    <div ref="macro_ref" class="com-chart" />
  </div>
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons')

import { basicFacet } from '@/api/dw/profile'

export default {
  data() {
    return {
      chartInstance: undefined,
      allData: undefined
    }
  },
  mounted() {
    this.initChart()
    this.loadData()
  },
  methods: {
    initChart() {
      this.chartInstance = echarts.init(this.$refs.macro_ref, this.theme)
      const name = this.$route.query.name
      const option = {
        title: {
          text: name,
          left: 20,
          top: 20
        },
        grid: {
          top: '20%',
          left: '3%',
          right: '6%',
          bottom: '3%',
          containLabel: true // grid 区域是否包含坐标轴的
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'line',
            z: 0,
            lineStyle: {
              color: '#2D3443'
            }
          },
          formatter: '标签：{b} <br> 人数：{c} '
        },
        xAxis: {
          type: 'value',
          splitLine: { show: false }
        },
        yAxis: {
          type: 'category',
          axisTick: {
            show: false
          },
          axisLabel: {
            formatter: function(value, index) {
              if (value.length > 3) {
                return value.substring(0, 3) + '...'
              } else {
                return value
              }
            }
          }
        },
        series: [
          {
            type: 'bar',
            label: {
              show: true,
              position: 'right',
              color: 'white'
            },
            barWidth: 66,
            itemStyle: {
              barBorderRadius: [0, 33, 33, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                // 百分之0状态之下的颜色值
                {
                  offset: 0,
                  color: '#5052EE'
                },
                // 百分之100状态之下的颜色值
                {
                  offset: 1,
                  color: '#AB6EE5'
                }
              ])
            }
          }
        ]
      }

      this.chartInstance.setOption(option)
    },
    loadData() {
      // 获取请求参数
      if (this.$route.query.id == null) {
        return
      }
      const tagId = this.$route.query.id
      basicFacet({ id: tagId }).then(response => {
        const { data: ret } = response.data
        this.allData = ret
        this.updateChart()
      })
    },
    updateChart() {
      // 降序
      this.allData.sort((a, b) => {
        return a.value - b.value
      })
      // 获取类目列表
      const categoryArray = this.allData.map(item => {
        return item.name
      })
      const option = {
        yAxis: {
          data: categoryArray
        },
        series: [
          {
            data: this.allData
          }
        ]
      }
      this.chartInstance.setOption(option)
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.com-container {
  .com-chart {
    width: 100%;
    height: 80vh;
  }
}
</style>
