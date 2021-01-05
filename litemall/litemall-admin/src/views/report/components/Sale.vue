<template>
  <div class="com-container">
    <div ref="sale_ref" class="com-chart" />
  </div>
</template>
<script>
import echarts from 'echarts'
import '@/assets/theme/chalk'
require('echarts/theme/macarons')
import { mapState } from 'vuex'
import { saleTopN } from '@/api/dw/report'

export default {
  data() {
    return {
      chartInstance: undefined,
      allData: undefined
    }
  },
  computed: {
    ...mapState({
      theme: state => state.theme.theme
    })
  },
  mounted() {
    this.initChart()
    this.loadData()
    window.addEventListener('resize', this.screenAdapter)
    this.screenAdapter()
  },
  destroyed() {
    window.removeEventListener('resize', this.screenAdapter)
  },
  methods: {
    initChart() {
      this.chartInstance = echarts.init(this.$refs.sale_ref, this.theme)
      const option = {
        title: {
          text: '▎ 销量排行',
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
          formatter: '商品：{b} <br> 销量：{c} 件'
        },
        xAxis: {
          type: 'value',
          splitLine: { show: false },
          axisLine: { show: false },
          axisTick: { show: false }
        },
        yAxis: {
          type: 'category',
          axisTick: {
            show: false
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
      saleTopN().then(response => {
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
    },
    screenAdapter() {
      const titleFontSize = (this.$refs.sale_ref.offsetWidth / 100) * 3.6
      const option = {
        title: {
          textStyle: {
            fontSize: titleFontSize
          }
        },
        tooltip: {
          axisPointer: {
            lineStyle: {
              width: titleFontSize
            }
          }
        },
        series: [
          {
            barWidth: titleFontSize,
            itemStyle: {
              barBorderRadius: [0, titleFontSize / 2, titleFontSize / 2, 0]
            }
          }
        ]
      }

      this.chartInstance.setOption(option)
      this.chartInstance.resize()
    }
  }
}
</script>

<style lang="sass" scoped>
</style>
