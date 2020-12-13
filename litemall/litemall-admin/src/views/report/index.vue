<template>
  <div class="dashboard-editor-container">
    <div class="header">
      <h1 class="header-title">销售情况大屏</h1>
    </div>
    <div class="wrapper">
      <div class="content">
        <div class="col col-l">
          <div class="xpanel-wrapper xpanel-wrapper-40">
            <div class="xpanel xpanel-l-t">
              <div class="title" />
            </div>
          </div>
          <div class="xpanel-wrapper xpanel-wrapper-60">
            <div class="xpanel xpanel-l-b">
              <div class="title">用户行为转换率</div>
              <div id="uacChart" class="uac-content" />
            </div>
          </div>
        </div>
        <div class="col col-c">
          <div class="xpanel-wrapper xpanel-wrapper-75">
            <div id="region-map" class="xpanel no-bg" />
          </div>
          <div class="xpanel-wrapper xpanel-wrapper-25">
            <div class="xpanel xpanel-c-b">
              <div class="title title-long" />
            </div>
          </div>
        </div>
        <div class="col col-r">
          <div class="xpanel-wrapper xpanel-wrapper-25">
            <div class="xpanel xpanel-r-t">
              <div class="title" />
            </div>
          </div>
          <div class="xpanel-wrapper xpanel-wrapper-30">
            <div class="xpanel xpanel-r-m">
              <div class="title" />
            </div>
          </div>
          <div class="xpanel-wrapper xpanel-wrapper-45">
            <div class="xpanel xpanel-r-b">
              <div class="title" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import echarts from 'echarts'
import 'echarts/map/js/china.js'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'
import { chartRegion, uaConvertList } from '@/api/dw/report'

const geoCoordMap = {
  '海门市': [121.15, 31.89],
  '鄂尔多斯市': [109.781327, 39.608266],
  '招远市': [120.38, 37.35],
  '舟山市': [122.207216, 29.985295],
  '齐齐哈尔市': [123.97, 47.33],
  '盐城市': [120.13, 33.38],
  '赤峰市': [118.87, 42.28],
  '青岛市': [120.33, 36.07],
  '乳山市': [121.52, 36.89],
  '金昌市': [102.188043, 38.520089],
  '泉州市': [118.58, 24.93],
  '莱西市': [120.53, 36.86],
  '日照市': [119.46, 35.42],
  '胶南市': [119.97, 35.88],
  '南通市': [121.05, 32.08],
  '拉萨市': [91.11, 29.97],
  '云浮市': [112.02, 22.93],
  '梅州市': [116.1, 24.55],
  '文登市': [122.05, 37.2],
  '上海市': [121.48, 31.22],
  '攀枝花市': [101.718637, 26.582347],
  '威海市': [122.1, 37.5],
  '承德市': [117.93, 40.97],
  '厦门市': [118.1, 24.46],
  '汕尾': [115.375279, 22.786211],
  '潮州': [116.63, 23.68],
  '丹东': [124.37, 40.13],
  '太仓': [121.1, 31.45],
  '曲靖': [103.79, 25.51],
  '烟台市': [121.39, 37.52],
  '福州市': [119.3, 26.08],
  '瓦房店': [121.979603, 39.627114],
  '即墨': [120.45, 36.38],
  '抚顺市': [123.97, 41.97],
  '玉溪': [102.52, 24.35],
  '张家口市': [114.87, 40.82],
  '阳泉市': [113.57, 37.85],
  '莱州市': [119.942327, 37.177017],
  '湖州市': [120.1, 30.86],
  '汕头市': [116.69, 23.39],
  '昆山市': [120.95, 31.39],
  '宁波市': [121.56, 29.86],
  '湛江市': [110.359377, 21.270708],
  '揭阳市': [116.35, 23.55],
  '荣成市': [122.41, 37.16],
  '连云港市': [119.16, 34.59],
  '葫芦岛市': [120.836932, 40.711052],
  '常熟市': [120.74, 31.64],
  '东莞市': [113.75, 23.04],
  '河源市': [114.68, 23.73],
  '淮安市': [119.15, 33.5],
  '泰州市': [119.9, 32.49],
  '南宁市': [108.33, 22.84],
  '营口市': [122.18, 40.65],
  '惠州市': [114.4, 23.09],
  '江阴市': [120.26, 31.91],
  '蓬莱市': [120.75, 37.8],
  '韶关市': [113.62, 24.84],
  '嘉峪关市': [98.289152, 39.77313],
  '广州市': [113.23, 23.16],
  '延安市': [109.47, 36.6],
  '太原市': [112.53, 37.87],
  '清远市': [113.01, 23.7],
  '中山市': [113.38, 22.52],
  '昆明市': [102.73, 25.04],
  '寿光市': [118.73, 36.86],
  '盘锦市': [122.070714, 41.119997],
  '长治市': [113.08, 36.18],
  '深圳市': [114.07, 22.62],
  '珠海市': [113.52, 22.3],
  '宿迁市': [118.3, 33.96],
  '咸阳市': [108.72, 34.36],
  '铜川市': [109.11, 35.09],
  '平度市': [119.97, 36.77],
  '佛山市': [113.11, 23.05],
  '海口市': [110.35, 20.02],
  '江门市': [113.06, 22.61],
  '章丘市': [117.53, 36.72],
  '肇庆市': [112.44, 23.05],
  '大连市': [121.62, 38.92],
  '临汾市': [111.5, 36.08],
  '吴江市': [120.63, 31.16],
  '石嘴山市': [106.39, 39.04],
  '沈阳市': [123.38, 41.8],
  '苏州市': [120.62, 31.32],
  '茂名市': [110.88, 21.68],
  '嘉兴市': [120.76, 30.77],
  '长春市': [125.35, 43.88],
  '胶州市': [120.03336, 36.264622],
  '银川市': [106.27, 38.47],
  '张家港市': [120.555821, 31.875428],
  '三门峡市': [111.19, 34.76],
  '锦州市': [121.15, 41.13],
  '南昌市': [115.89, 28.68],
  '柳州市': [109.4, 24.33],
  '三亚市': [109.511909, 18.252847],
  '自贡市': [104.778442, 29.33903],
  '吉林市': [126.57, 43.87],
  '阳江市': [111.95, 21.85],
  '泸州市': [105.39, 28.91],
  '西宁市': [101.74, 36.56],
  '宜宾市': [104.56, 29.77],
  '呼和浩特市': [111.65, 40.82],
  '成都市': [104.06, 30.67],
  '大同市': [113.3, 40.12],
  '镇江市': [119.44, 32.2],
  '桂林市': [110.28, 25.29],
  '张家界市': [110.479191, 29.117096],
  '宜兴市': [119.82, 31.36],
  '北海市': [109.12, 21.49],
  '西安市': [108.95, 34.27],
  '金坛市': [119.56, 31.74],
  '东营市': [118.49, 37.46],
  '牡丹江市': [129.58, 44.6],
  '遵义市': [106.9, 27.7],
  '绍兴市': [120.58, 30.01],
  '扬州市': [119.42, 32.39],
  '常州市': [119.95, 31.79],
  '潍坊市': [119.1, 36.62],
  '重庆市': [106.54, 29.59],
  '台州市': [121.420757, 28.656386],
  '南京市': [118.78, 32.04],
  '滨州市': [118.03, 37.36],
  '贵阳市': [106.71, 26.57],
  '无锡市': [120.29, 31.59],
  '本溪市': [123.73, 41.3],
  '克拉玛依市': [84.77, 45.59],
  '渭南市': [109.5, 34.52],
  '马鞍山市': [118.48, 31.56],
  '宝鸡市': [107.15, 34.38],
  '焦作市': [113.21, 35.24],
  '句容市': [119.16, 31.95],
  '北京市': [116.46, 39.92],
  '徐州市': [117.2, 34.26],
  '衡水市': [115.72, 37.72],
  '包头市': [110, 40.58],
  '绵阳市': [104.73, 31.48],
  '乌鲁木齐市': [87.68, 43.77],
  '枣庄市': [117.57, 34.86],
  '杭州市': [120.19, 30.26],
  '淄博市': [118.05, 36.78],
  '鞍山市': [122.85, 41.12],
  '溧阳市': [119.48, 31.43],
  '库尔勒市': [86.06, 41.68],
  '安阳市': [114.35, 36.1],
  '开封市': [114.35, 34.79],
  '济南市': [117, 36.65],
  '德阳市': [104.37, 31.13],
  '温州市': [120.65, 28.01],
  '九江市': [115.97, 29.71],
  '邯郸市': [114.47, 36.6],
  '临安市': [119.72, 30.23],
  '兰州市': [103.73, 36.03],
  '沧州市': [116.83, 38.33],
  '临沂市': [118.35, 35.05],
  '南充市': [106.110698, 30.837793],
  '天津市': [117.2, 39.13],
  '富阳市': [119.95, 30.07],
  '泰安市': [117.13, 36.18],
  '诸暨市': [120.23, 29.71],
  '郑州市': [113.65, 34.76],
  '哈尔滨市': [126.63, 45.75],
  '聊城市': [115.97, 36.45],
  '芜湖市': [118.38, 31.33],
  '唐山市': [118.02, 39.63],
  '平顶山市': [113.29, 33.75],
  '邢台市': [114.48, 37.05],
  '德州市': [116.29, 37.45],
  '济宁市': [116.59, 35.38],
  '荆州市': [112.239741, 30.335165],
  '宜昌市': [111.3, 30.7],
  '义乌市': [120.06, 29.32],
  '丽水市': [119.92, 28.45],
  '洛阳市': [112.44, 34.7],
  '秦皇岛市': [119.57, 39.95],
  '株洲市': [113.16, 27.83],
  '石家庄市': [114.48, 38.03],
  '莱芜市': [117.67, 36.19],
  '常德市': [111.69, 29.05],
  '保定市': [115.48, 38.85],
  '湘潭市': [112.91, 27.87],
  '金华市': [119.64, 29.12],
  '岳阳市': [113.09, 29.37],
  '长沙市': [113, 28.21],
  '衢州市': [118.88, 28.97],
  '廊坊市': [116.7, 39.53],
  '菏泽市': [115.480656, 35.23375],
  '合肥市': [117.27, 31.86],
  '武汉市': [114.31, 30.52],
  '大庆市': [125.03, 46.58]
}

export default {
  mixins: [resize],
  data() {
    return {
      chart: null,
      uacChart: null,
      listQuery: {
        date: '2020-12-12'
      },
      regions: null,
      convert: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.getList()
    })
  },
  beforeDestroy() {
    if (!this.chart || !this.uacChart) {
      return
    }
    this.chart.dispose()
    this.chart = null

    this.uacChart.dispose()
    this.uacChart = null
  },
  methods: {
    getList() {
      chartRegion(this.listQuery).then(reponse => {
        this.regions = reponse.data.data
        // 初始化地图
        this.initChart()
      })

      uaConvertList(this.listQuery).then(reponse => {
        this.convert = reponse.data.data
        // 初始化地图
        this.initUacChart()
      })
    },
    convertData(data) {
      var res = []
      for (var i = 0; i < data.length; i++) {
        var geoCoord = geoCoordMap[data[i].cityName]
        if (geoCoord) {
          res.push({
            name: data[i].cityName,
            value: geoCoord.concat(data[i].orderCount)
          })
        }
      }
      return res
    },
    initChart() {
      this.chart = echarts.init(document.getElementById('region-map'), 'macarons')

      // 地图
      var option = {
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
          left: 100,
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
            data: this.convertData(this.regions)
          },
          // 地图中闪烁的点
          {
            //  name: 'Top 5',
            type: 'effectScatter',
            coordinateSystem: 'geo',
            data: this.convertData(this.regions.sort(function(a, b) {
              return b.orderCount - a.orderCount
            }).slice(0, 20)),
            symbolSize: function(val) {
              return val[2] // / 10
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
                color: '#1DE9B6',
                shadowBlur: 10,
                shadowColor: '#1DE9B6'
              }
            },
            zlevel: 1
          }
        ]
      }
      // 设置属性
      this.chart.setOption(option)
    },
    initUacChart() {
      this.uacChart = echarts.init(document.getElementById('uacChart'), 'macarons')

      // 地图
      var option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}%'
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
      this.uacChart.setOption(option)
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-editor-container{
	width:100%;
	position: absolute;
  top: 0px;
  bottom: 0px;
  background-color:#0b0f34;
  overflow-x:hidden;
  overflow-y:auto;
  margin:0 auto;
  min-width:375px;
  max-width:1920px;
	/********** Layout **********/
	.header {
		position:relative;
		height:42px;
		box-sizing:border-box;
	}
	.header-title {
		margin:0;
		padding:0;
		line-height:64px;
		text-align:center;
		font-size:32px;
		font-weight:400;
		color:#e9c29d;
	}
	.wrapper {
		position:absolute;
		top:36px;
		bottom:10px;
		left:10px;
		right:10px;
		min-height:500px;
		background:url("img/wrapper-bg.png") no-repeat;
		background-size:100% 100%;
		box-sizing:border-box;
	}
	/* PC */
	@media (max-width:1919px) {
		.header {height:36px;}
		.header-title {line-height:42px;font-size:22px;}
		.wrapper {top:22px;}
	}
	/* Mobile */
	@media (max-width:1279px) {
		.header-title {max-width:96%;}
		.wrapper {background:none;}
	}

	/********** Content **********/
	.content {
		display:-webkit-flex;
		display:-ms-flexbox;
		display:flex;
		padding:25px 15px;
		height:100%;
		min-height:100%;
		box-sizing:border-box;
	}
	.col {margin:0 10px;height:100%;}
	.col-l {-webkit-flex:2;-ms-flex:2;flex:2}
	.col-c {-webkit-flex:3;-ms-flex:3;flex:3;}
	.col-r {-webkit-flex:2;-ms-flex:2;flex:2;}
	/* PC */
	@media (max-width:1919px) {
		.content {padding:5px 0;}
	}
	/* Mobile */
	@media (max-width:1279px) {
		.content {
			-webkit-flex-direction:column;
			-ms-flex-direction:column;
			flex-direction:column;
		}
		.col {margin:5px 0;}
		.col-l,
		.col-c,
		.col-r {-webkit-flex:none;-ms-flex:none;flex:none;}
	}
	.xpanel-wrapper {padding-bottom:10px;box-sizing:border-box;}
	.xpanel-wrapper {height:100%;}
	.xpanel-wrapper-25 {height:25%;}
	.xpanel-wrapper-30 {height:30%;}
	.xpanel-wrapper-40 {height:40%;}
	.xpanel-wrapper-45 {height:45%;}
	.xpanel-wrapper-60 {height:60%;}
	.xpanel-wrapper-75 {height:75%;}
	.xpanel {
		height:100%;
		min-height:100px;
		background-repeat:no-repeat;
		background-size:100% 100%;
		box-sizing:border-box;
	}
	.xpanel-l-t {background-image:url("img/panel-l-t.png");}
	.xpanel-l-b {background-image:url("img/panel-l-b.png");}
	.xpanel-c-b {background-image:url("img/panel-c-b.png");}
	.xpanel-r-t {background-image:url("img/panel-r-t.png");}
	.xpanel-r-m {background-image:url("img/panel-r-m.png");}
	.xpanel-r-b {background-image:url("img/panel-r-b.png");}
	.xpanel .title {
		padding-left:24px;
		height:44px;
		line-height:44px;
		font-weight:normal;
		color:#fff;
		background-image:url("img/title-bg.png");
		background-repeat:no-repeat;
		background-size:100% 100%;
	}
	.xpanel .title-long {background-image:url("img/title-bg-long.png");}
	/* PC */
	@media (max-width:1919px) {
		.xpanel .title {padding-left:20px;height:36px;line-height:36px;}
	}

	/* tool */
	.fill-h {height:100% !important;min-height:100% !important;}
	.no-margin {margin:0 !important;}
	.no-padding {padding:0 !important;}
	.no-bg {background:none !important;}
	.no-border {border:0 !important;}

	/* scrollbar */
	::-webkit-scrollbar {width:0;height:0;}
	::-webkit-scrollbar-track {background-color:transparent;}
	::-webkit-scrollbar-thumb {border-radius:5px;background-color:rgba(0, 0, 0, 0.3);}

	.uac-content{
		height: 400px;
	}
}
</style>
