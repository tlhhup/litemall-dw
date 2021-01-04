<template>
  <div class="com-container" @dblclick="reserveMap">
    <div ref="map_ref" class="com-chart" />
  </div>
</template>

<script>
import echarts from 'echarts'
import '@/assets/theme/chalk'
require('echarts/theme/macarons')
import { mapState } from 'vuex'
import { getProvinceMapInfo } from '@/utils/mapUtil'
import { mapJson, chartRegion } from '@/api/dw/report'

export default {
  data() {
    return {
      chartInstance: undefined,
      allData: undefined,
      loadedMap: {},
      mapStack: [],
      currentMap: 'china',
      currentMapId: '100000',
      listQuery: {
        date: undefined,
        type: 0,
        name: undefined
      }
    }
  },
  computed: {
    ...mapState(['theme'])
  },
  mounted() {
    this.initChart()
    this.loadData()
    window.addEventListener('resize', this.screenAdapter)
  },
  destroyed() {
    window.removeEventListener('resize', this.screenAdapter)
  },
  methods: {
    async initChart() {
      this.chartInstance = echarts.init(this.$refs.map_ref, this.theme)
      const ret = await mapJson(this.currentMapId)
      const mapJsonData = ret.data.data

      echarts.registerMap('china', mapJsonData)
      const option = {
        title: {
          text: '▎ 订单分布',
          left: 20,
          top: 20
        },
        tooltip: {
          formatter: function(arg) {
            if (arg.data) {
              return `${arg.data.name} 昨日<br>
              订单:${arg.data.value}<br>
              金额:${arg.data.amount}`
            } else {
              return `${arg.name}`
            }
          }
        },
        geo: {
          type: 'map',
          map: 'china',
          top: '5%',
          bottom: '5%',
          itemStyle: {
            borderColor: '#333'
          }
        },
        legend: {
          left: '5%',
          bottom: '5%',
          orient: 'vertical'
        }
      }
      this.chartInstance.setOption(option)
      // 添加点击事件
      this.chartInstance.on('click', async arg => {
        const provinceInfo = getProvinceMapInfo(arg.name)
        if (provinceInfo) {
          // 如果没有加载过
          if (!this.loadedMap[provinceInfo.key]) {
            const ret = await mapJson(provinceInfo.key)
            const mapJsonData = ret.data.data
            // 记录历史和注册地图
            this.loadedMap[provinceInfo.key] = mapJsonData
            echarts.registerMap(arg.name, mapJsonData)
            // 重新加载数据
            this.listQuery.name = arg.name
            this.listQuery.type++
            this.loadData()
          }
          const option = {
            geo: {
              map: arg.name
            }
          }
          this.chartInstance.setOption(option)
          // 记录
          this.mapStack.push(this.currentMap)
          this.currentMap = arg.name
        }
      })
    },
    loadData() {
      chartRegion(this.listQuery).then(response => {
        const { data: ret } = response.data
        this.allData = ret
        this.updateChart()
      })
    },
    updateChart() {
      let minOrder = 0
      let maxOrder = 0
      const showData = this.allData.map(item => {
        if (item.orderCount > maxOrder) {
          maxOrder = item.orderCount
        }
        if (item.orderCount < minOrder) {
          minOrder = item.orderCount
        }
        return {
          name: item.name,
          value: item.orderCount,
          amount: item.orderAmount
        }
      })

      const option = {
        series: [
          {
            type: 'map',
            geoIndex: 0,
            data: showData
          }
        ],
        visualMap: [
          {
            type: 'continuous',
            min: minOrder,
            max: maxOrder,
            calculable: true,
            inRange: {
              color: ['white', 'red']
            }
          }
        ]
      }
      this.chartInstance.setOption(option)
    },
    screenAdapter() {
      const titleFontSize = (this.$refs.map_ref.offsetWidth / 100) * 3.6
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
    reserveMap() {
      const mapName = this.mapStack.pop()
      if (mapName) {
        const option = {
          geo: {
            map: mapName
          }
        }
        this.chartInstance.setOption(option)
        // 更新current
        this.currentMap = mapName
        // 重新加载
        this.listQuery.name = mapName
        this.listQuery.type--
        this.loadData()
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
</style>
