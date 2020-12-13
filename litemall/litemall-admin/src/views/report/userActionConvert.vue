<template>
  <div class="region-map" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'
import { uaConvertList } from '@/api/dw/report'

export default {
  mixins: [resize],
  data() {
    return {
      chart: null,
      listQuery: {
        date: '2020-12-09'
      },
      convert: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.getList()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    getList() {
      uaConvertList(this.listQuery).then(reponse => {
        this.convert = reponse.data.data
        // 初始化地图
        this.initChart()
      })
    },
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')

      // 地图
      var option = {
      	title: {
      		text: '漏斗图',
      		subtext: '纯属虚构'
      	},
      	tooltip: {
      		trigger: 'item',
      		formatter: '{a} <br/>{b} : {c}%'
      	},
      	toolbox: {
      		feature: {
      			dataView: { readOnly: false },
      			restore: {},
      			saveAsImage: {}
      		}
      	},
      	legend: {
      		data: ['访问', '加购', '订单', '支付']
      	},
      	series: [
      	  {
      		  name: '漏斗图',
      		  type: 'funnel',
      		  left: '10%',
      		  top: 60,
            bottom: 60,
            width: '80%',
            min: 0,
            max: 100,
            minSize: '0%',
            maxSize: '100%',
            sort: 'none',
            gap: 2,
            label: {
            	show: true,
            	position: 'inside'
            },
            labelLine: {
            	length: 10,
            	lineStyle: {
            		width: 1,
            		type: 'solid'
            	}
            },
            itemStyle: {
            	borderColor: '#fff',
            	borderWidth: 1
            },
            emphasis: {
            	label: {
            		fontSize: 20
            	}
            },
            data: this.convert
          }
        ]
      }
      // 设置属性
      this.chart.setOption(option)
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
.region-map{
	width: 100%;
	position: absolute;
	top: 0px;
	bottom: 0px;
}

</style>
