<template>
  <div class="region-map" />
</template>

<script>
import echarts from 'echarts'
import 'echarts/map/js/china.js'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

const geoCoordMap = {
  '杭州': [119.5313, 29.8773],
  '苏州': [118.8062, 31.9208],
  '上海': [121.4648, 31.2891],
  '天津': [117.4219, 39.4189],
  '深圳': [114.072026, 22.552194],
  '成都': [103.9526, 30.7617],
  '郑州': [113.4668, 34.6234],
  '宁波': [121.640618, 29.86206],
  '合肥': [117.29, 32.0581],
  '重庆': [108.384366, 30.439702],
  '广州': [113.12244, 23.009505],
  '大连': [123.1238, 42.1216],
  '青岛': [117.1582, 36.8701],
  '北京': [116.4551, 40.2539],
  '义乌': [120.067209, 29.346921],
  '东莞': [113.764742, 23.02039],
  '长沙': [113.0823, 28.2568],
  '贵阳': [106.6992, 26.7682],
  '珠海': [113.556111, 22.250876],
  '威海': [122.109148, 37.516889],
  '泉州': [118.58, 24.93],
  '赤峰': [118.87, 42.28],
  '厦门': [118.1, 24.46],
  '福州': [119.3, 26.08],
  '抚顺': [123.97, 41.97],
  '汕头': [116.69, 23.39],
  '海口': [110.35, 20.02],
  '岳阳': [113.09, 29.37],
  '武汉': [114.31, 30.52],
  '唐山': [118.02, 39.63],
  '石家庄': [114.48, 38.03],
  '哈尔滨': [126.63, 45.75],
  '兰州': [103.73, 36.03],
  '呼和浩特': [111.65, 40.82],
  '南昌': [115.89, 28.68],
  '佛山': [113.11, 23.05],
  '烟台': [121.39, 37.52]
}
const colors = [
  ['#1DE9B6', '#F46E36', '#04B9FF', '#5DBD32', '#FFC809', '#FB95D5', '#BDA29A', '#6E7074', '#546570', '#C4CCD3'],
  ['#37A2DA', '#67E0E3', '#32C5E9', '#9FE6B8', '#FFDB5C', '#FF9F7F', '#FB7293', '#E062AE', '#E690D1', '#E7BCF3', '#9D96F5', '#8378EA', '#8378EA'],
  ['#DD6B66', '#759AA0', '#E69D87', '#8DC1A9', '#EA7E53', '#EEDD78', '#73A373', '#73B9BC', '#7289AB', '#91CA8C', '#F49F42']
]
const colorIndex = 0
export default {
  mixins: [resize],
  data() {
    return {
      chart: null,
      regions: {
        dates: ['2013', '2014', '2015', '2016'],
        orders: [
          {
            '杭州': 10,
            '苏州': 2,
            '上海': 21,
            '天津': 4,
            '深圳': 7,
            '郑州': 7,
            '成都': 5,
            '宁波': 2,
            '合肥': 1,
            '重庆': 3,
            '广州': 19,
            '大连': 1,
            '青岛': 2,
            '北京': 16,
            '义乌': 2,
            '东莞': 1,
            '长沙': 3,
            '贵阳': 0,
            '珠海': 0,
            '威海': 0,
            '南昌': 1,
            '西安': 2,
            '南京': 6,
            '海口': 0,
            '厦门': 3,
            '沈阳': 3,
            '无锡': 0,
            '呼和浩特': 0,
            '长春': 0,
            '哈尔滨': 1,
            '武汉': 5,
            '南宁': 1,
            '昆明': 1,
            '兰州': 0,
            '唐山': 0,
            '石家庄': 2,
            '太原': 1,
            '赤峰': 0,
            '抚顺': 0,
            '珲春': 0,
            '绥芬河': 0,
            '徐州': 0,
            '南通': 1,
            '温州': 2,
            '绍兴': 0,
            '芜湖': 0,
            '福州': 5,
            '泉州': 2,
            '赣州': 2,
            '济南': 3,
            '烟台': 0,
            '洛阳': 1,
            '黄石': 0,
            '岳阳': 0,
            '汕头': 0,
            '佛山': 0,
            '泸州': 0,
            '海东': 0,
            '银川': 0
          },
          {
            '杭州': 131,
            '苏州': 51,
            '上海': 114,
            '天津': 58,
            '深圳': 104,
            '郑州': 66,
            '成都': 35,
            '宁波': 59,
            '合肥': 28,
            '重庆': 68,
            '广州': 120,
            '大连': 24,
            '青岛': 58,
            '北京': 118,
            '义乌': 36,
            '东莞': 46,
            '长沙': 34,
            '贵阳': 8,
            '珠海': 11,
            '威海': 7,
            '南昌': 24,
            '西安': 35,
            '南京': 42,
            '海口': 6,
            '厦门': 59,
            '沈阳': 18,
            '无锡': 21,
            '呼和浩特': 7,
            '长春': 13,
            '哈尔滨': 16,
            '武汉': 52,
            '南宁': 14,
            '昆明': 10,
            '兰州': 5,
            '唐山': 3,
            '石家庄': 24,
            '太原': 13,
            '赤峰': 0,
            '抚顺': 0,
            '珲春': 1,
            '绥芬河': 3,
            '徐州': 5,
            '南通': 12,
            '温州': 32,
            '绍兴': 11,
            '芜湖': 3,
            '福州': 72,
            '泉州': 47,
            '赣州': 3,
            '济南': 40,
            '烟台': 14,
            '洛阳': 7,
            '黄石': 1,
            '岳阳': 1,
            '汕头': 8,
            '佛山': 31,
            '泸州': 0,
            '海东': 0,
            '银川': 37
          },
          {
            '杭州': 311,
            '苏州': 174,
            '上海': 308,
            '天津': 192,
            '深圳': 304,
            '郑州': 194,
            '成都': 179,
            '宁波': 191,
            '合肥': 130,
            '重庆': 189,
            '广州': 345,
            '大连': 139,
            '青岛': 182,
            '北京': 336,
            '义乌': 136,
            '东莞': 159,
            '长沙': 151,
            '贵阳': 81,
            '珠海': 96,
            '威海': 80,
            '南昌': 112,
            '西安': 163,
            '南京': 155,
            '海口': 59,
            '厦门': 170,
            '沈阳': 102,
            '无锡': 110,
            '呼和浩特': 54,
            '长春': 76,
            '哈尔滨': 113,
            '武汉': 187,
            '南宁': 104,
            '昆明': 100,
            '兰州': 48,
            '唐山': 48,
            '石家庄': 110,
            '太原': 80,
            '赤峰': 8,
            '抚顺': 7,
            '珲春': 19,
            '绥芬河': 16,
            '徐州': 63,
            '南通': 78,
            '温州': 111,
            '绍兴': 88,
            '芜湖': 29,
            '福州': 189,
            '泉州': 148,
            '赣州': 31,
            '济南': 161,
            '烟台': 85,
            '洛阳': 49,
            '黄石': 10,
            '岳阳': 15,
            '汕头': 74,
            '佛山': 153,
            '泸州': 10,
            '海东': 0,
            '银川': 34
          },
          {
            '杭州': 296,
            '苏州': 184,
            '上海': 332,
            '天津': 136,
            '深圳': 327,
            '郑州': 208,
            '成都': 235,
            '宁波': 200,
            '合肥': 142,
            '重庆': 191,
            '广州': 327,
            '大连': 154,
            '青岛': 168,
            '北京': 358,
            '义乌': 133,
            '东莞': 166,
            '长沙': 159,
            '贵阳': 81,
            '珠海': 86,
            '威海': 58,
            '南昌': 118,
            '西安': 180,
            '南京': 170,
            '海口': 78,
            '厦门': 160,
            '沈阳': 114,
            '无锡': 119,
            '呼和浩特': 80,
            '长春': 92,
            '哈尔滨': 123,
            '武汉': 190,
            '南宁': 122,
            '昆明': 128,
            '兰州': 69,
            '唐山': 60,
            '石家庄': 118,
            '太原': 93,
            '赤峰': 16,
            '抚顺': 9,
            '珲春': 21,
            '绥芬河': 16,
            '徐州': 78,
            '南通': 93,
            '温州': 122,
            '绍兴': 95,
            '芜湖': 36,
            '福州': 187,
            '泉州': 148,
            '赣州': 47,
            '济南': 161,
            '烟台': 87,
            '洛阳': 55,
            '黄石': 11,
            '岳阳': 26,
            '汕头': 78,
            '佛山': 150,
            '泸州': 10,
            '海东': 0,
            '银川': 45
          }
        ]
      },
      categoryData: [],
      barData: [],
      mapData: []
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initData()
      this.initChart()
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
    convertData(data) {
      var res = []
      for (var i = 0; i < data.length; i++) {
        var geoCoord = geoCoordMap[data[i].name]
        if (geoCoord) {
          res.push({
            name: data[i].name,
            value: geoCoord.concat(data[i].value)
          })
        }
      }
      return res
    },
    initData() {
      for (var i = 0; i < this.regions.dates.length; i++) {
        this.mapData.push([])
        for (var key in geoCoordMap) {
          this.mapData[i].push({
            'year': this.regions.dates[i],
            'name': key,
            'value': this.regions.orders[i][key]
          })
        }
      }

      for (var index = 0; index < this.mapData.length; index++) {
        this.mapData[index].sort(function sortNumber(a, b) {
          return a.value - b.value
        })
        this.barData.push([])
        this.categoryData.push([])
        for (var j = 0; j < this.mapData[index].length; j++) {
          this.barData[index].push(this.mapData[index][j].value)
          this.categoryData[index].push(this.mapData[index][j].name)
        }
      }
    },
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')

      var optionMaps = {
        timeline: {
          data: this.regions.dates,
          axisType: 'category',
          autoPlay: true,
          playInterval: 3000,
          left: '10%',
          right: '10%',
          bottom: '3%',
          width: '80%',
          label: {
            normal: {
              textStyle: {
                color: '#ddd'
              }
            },
            emphasis: {
              textStyle: {
                color: '#fff'
              }
            }
          },
          symbolSize: 10,
          lineStyle: {
            color: '#555'
          },
          checkpointStyle: {
            borderColor: '#777',
            borderWidth: 2
          },
          controlStyle: {
            showNextBtn: true,
            showPrevBtn: true,
            normal: {
              color: '#666',
              borderColor: '#666'
            },
            emphasis: {
              color: '#aaa',
              borderColor: '#aaa'
            }
          }
        },
        baseOption: {
          animation: true,
          animationDuration: 1000,
          animationEasing: 'cubicInOut',
          animationDurationUpdate: 1000,
          animationEasingUpdate: 'cubicInOut',
          grid: {
            right: '1%',
            top: '15%',
            bottom: '10%',
            width: '20%'
          },
          tooltip: {
            trigger: 'axis', // hover触发器
            axisPointer: { // 坐标轴指示器，坐标轴触发有效
              type: 'shadow', // 默认为直线，可选为：'line' | 'shadow'
              shadowStyle: {
                color: 'rgba(150,150,150,0.1)' // hover颜色
              }
            }
          },
          geo: {
            show: true,
            map: 'china',
            roam: false, // 禁止缩放
            zoom: 1,
            center: [113.83531246, 34.0267395887],
            label: {
              emphasis: {
                show: false
              }
            },
            itemStyle: {
              normal: {
                borderColor: 'rgba(147, 235, 248, 1)',
                borderWidth: 1,
                areaColor: {
                  type: 'radial',
                  x: 0.5,
                  y: 0.5,
                  r: 0.8,
                  colorStops: [{
                    offset: 0,
                    color: 'rgba(147, 235, 248, 0)' // 0% 处的颜色
                  }, {
                    offset: 1,
                    color: 'rgba(147, 235, 248, .2)' // 100% 处的颜色
                  }],
                  globalCoord: false // 缺省为 false
                },
                shadowColor: 'rgba(128, 217, 248, 1)',
                shadowOffsetX: -2,
                shadowOffsetY: 2,
                shadowBlur: 10
              },
              emphasis: {
                areaColor: '#389BB7',
                borderWidth: 0
              }
            }
          }
        },
        options: []
      }
      // 处理每个日期的数据
      for (var n = 0; n < this.regions.dates.length; n++) {
        optionMaps.options.push({
          backgroundColor: '#013954',
          title: [
            {
              text: '全国订单大数据',
              left: '25%',
              top: '7%',
              textStyle: {
                color: '#fff',
                fontSize: 25
              }
            },
            {
              id: 'statistic',
              text: this.regions.dates[n] + '数据统计情况',
              left: '75%',
              top: '8%',
              textStyle: {
                color: '#fff',
                fontSize: 25
              }
            }
          ],
          xAxis: {
            type: 'value',
            scale: true,
            position: 'top',
            min: 0,
            boundaryGap: false,
            splitLine: {
              show: false
            },
            axisLine: {
              show: false
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              margin: 2,
              textStyle: {
                color: '#aaa'
              }
            }
          },
          yAxis: {
            type: 'category',
            nameGap: 16,
            axisLine: {
              show: true,
              lineStyle: {
                color: '#ddd'
              }
            },
            axisTick: {
              show: false,
              lineStyle: {
                color: '#ddd'
              }
            },
            axisLabel: {
              interval: 0,
              textStyle: {
                color: '#ddd'
              }
            },
            data: this.categoryData[n]
          },
          series: [
            // 地图
            {
              type: 'map',
              map: 'china',
              geoIndex: 0,
              aspectScale: 0.75, // 长宽比
              showLegendSymbol: false, // 存在legend时显示
              label: {
                normal: {
                  show: false
                },
                emphasis: {
                  show: false,
                  textStyle: {
                    color: '#fff'
                  }
                }
              },
              roam: true,
              itemStyle: {
                normal: {
                  areaColor: '#031525',
                  borderColor: '#FFFFFF'
                },
                emphasis: {
                  areaColor: '#2B91B7'
                }
              },
              animation: false,
              data: this.mapData
            },
            // 地图中闪烁的点
            {
              //  name: 'Top 5',
              type: 'effectScatter',
              coordinateSystem: 'geo',
              data: this.convertData(this.mapData[n].sort(function(a, b) {
                return b.value - a.value
              }).slice(0, 20)),
              symbolSize: function(val) {
                return val[2] / 10
              },
              showEffectOn: 'render',
              rippleEffect: {
                brushType: 'stroke'
              },
              hoverAnimation: true,
              label: {
                normal: {
                  formatter: '{b}',
                  position: 'right',
                  show: true
                }
              },
              itemStyle: {
                normal: {
                  color: colors[colorIndex][n],
                  shadowBlur: 10,
                  shadowColor: colors[colorIndex][n]
                }
              },
              zlevel: 1
            },
            // 柱状图
            {
              zlevel: 1.5,
              type: 'bar',
              symbol: 'none',
              itemStyle: {
                normal: {
                  color: colors[colorIndex][n]
                }
              },
              data: this.barData[n]
            }
          ]
        })
      }
      // 设置属性
      this.chart.setOption(optionMaps)
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
