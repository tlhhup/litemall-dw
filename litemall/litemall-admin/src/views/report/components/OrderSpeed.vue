<template>
  <div class="com-container">
    <div ref="speed_ref" class="com-chart" />
  </div>
</template>

<script>
import echarts from 'echarts'
import '@/assets/theme/chalk'
require('echarts/theme/macarons')
import { mapState } from 'vuex'
import { orderSpeed } from '@/api/dw/report'

export default {
  data() {
    return {
      chartInstance: undefined,
      allData: undefined,
      timer: undefined
    }
  },
  computed: {
    ...mapState({
      // 模块名.状态名
      theme: state => state.theme.theme
    })
  },
  mounted() {
    this.initChart()
    this.loadData()
    window.addEventListener('resize', this.screenAdapter)
    this.screenAdapter()
    // 定时
    this.startIntval()
  },
  destroyed() {
    window.removeEventListener('resize', this.screenAdapter)
    clearInterval(this.timer)
  },
  methods: {
    initChart() {
      this.chartInstance = echarts.init(this.$refs.speed_ref, this.theme)
      const option = {
        title: {
          text: '▎ 实时订单',
          left: 20,
          top: 20
        },
        tooltip: {
          trigger: 'axis',
          formatter: '订单数为:{c}'
        },
        grid: {
          left: '3%',
          top: '30%',
          right: '6%',
          bottom: '2%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            type: 'line',
            smooth: true
          }
        ]
      }
      this.chartInstance.setOption(option)
    },
    loadData() {
      orderSpeed()
        .then(response => {
          const { data: ret } = response.data
          this.allData = ret
          this.updateChart()
        })
        .catch(response => {
          clearInterval(this.timer)
        })
    },
    updateChart() {
      // 时间
      const times = this.allData.map(item => {
        return item.date
      })
      // 数据
      const showData = this.allData.map(item => {
        return item.speed
      })
      const option = {
        xAxis: {
          data: times
        },
        series: [
          {
            data: showData,
            markPoint: {
              data: [
                {
                  type: 'max',
                  name: '最大值'
                }
              ]
            },
            markLine: {
              data: [
                {
                  name: '平均线',
                  type: 'average'
                }
              ]
            }
          }
        ]
      }
      this.chartInstance.setOption(option)
    },
    screenAdapter() {
      const titleFontSize = (this.$refs.speed_ref.offsetWidth / 100) * 3.6
      const option = {
        title: {
          textStyle: {
            fontSize: titleFontSize
          }
        },
        legend: {
          itemWidth: titleFontSize / 2,
          itemHeight: titleFontSize / 2,
          itemGap: titleFontSize / 2,
          textStyle: {
            fontSize: titleFontSize / 2
          }
        }
      }
      this.chartInstance.setOption(option)
      this.chartInstance.resize()
    },
    startIntval() {
      this.timer = setInterval(() => {
        this.loadData()
      }, 10 * 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
