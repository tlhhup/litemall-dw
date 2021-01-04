<template>
  <div class="com-container">
    <div ref="ua_ref" class="com-chart" />
  </div>
</template>

<script>
import echarts from 'echarts'
import '@/assets/theme/chalk'
require('echarts/theme/macarons')
import { uaConvertList } from '@/api/dw/report'
import { mapState } from 'vuex'

export default {
  data() {
    return {
      chart: null,
      listQuery: {
        date: undefined
      },
      convert: null
    }
  },
  computed: {
    ...mapState(['theme'])
  },
  mounted() {
    this.initChart()
    this.loadData()
    // 注册监听
    window.addEventListener('resize', this.screenAdapter)
    this.screenAdapter()
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  destroyed() {
    window.removeEventListener('resize', this.screenAdapter)
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.ua_ref, this.theme)

      var option = {
        title: {
          text: '▎ 漏斗图'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}%'
        },
        legend: {
          data: ['访问', '加购', '订单', '支付']
        },
        series: [
          {
            name: '用户行为',
            type: 'funnel',
            left: '10%',
            top: 60,
            bottom: 60,
            min: 0,
            max: 100,
            minSize: '0%',
            maxSize: '100%',
            sort: 'none',
            width: '80%',
            label: {
              show: true,
              position: 'inside'
            }
          }
        ]
      }
      // 设置属性
      this.chart.setOption(option)
    },
    loadData() {
      uaConvertList(this.listQuery).then(reponse => {
        this.convert = reponse.data.data
        this.updateChart()
      })
    },
    updateChart() {
      const option = {
        series: [
          {
            data: this.convert
          }
        ]
      }
      this.chart.setOption(option)
    },
    screenAdapter() {
      const titleFontSize = (this.$refs.ua_ref.offsetWidth / 100) * 3.6
      const option = {
        title: {
          textStyle: {
            fontSize: titleFontSize
          }
        },
        legend: {
          itemWidth: titleFontSize,
          itemHeight: titleFontSize,
          itemGap: titleFontSize / 2,
          textStyle: {
            fontSize: titleFontSize
          }
        }
      }
      this.chart.setOption(option)
      this.chart.resize()
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
</style>
