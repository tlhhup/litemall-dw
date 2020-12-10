<template>
  <div class="dashboard-editor-container">

    <el-row :gutter="40" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
          <div class="card-panel-icon-wrapper icon-people">
            <svg-icon icon-class="peoples" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">用户数量</div>
            <count-to :start-val="0" :end-val="userTotal" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('messages')">
          <div class="card-panel-icon-wrapper icon-message">
            <svg-icon icon-class="message" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">商品数量</div>
            <count-to :start-val="0" :end-val="goodsTotal" :duration="3000" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-icon-wrapper icon-money">
            <svg-icon icon-class="message" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">货品数量</div>
            <count-to :start-val="0" :end-val="productTotal" :duration="3200" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('shoppings')">
          <div class="card-panel-icon-wrapper icon-shoppingCard">
            <svg-icon icon-class="money" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">订单数量</div>
            <count-to :start-val="0" :end-val="orderTotal" :duration="3600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
    </el-row>
    <!-- report dashboard -->
    <el-row class="panel-group-report-query">
      <el-col>
        <div class="report-dec">
          <div class="report-title"><i class="el-icon-coin">整体看板</i></div>
          <div class="report-param">
            <el-radio-group v-model="listQuery.type" size="mini" @change="handleFilter">
              <el-radio-button label="0">日</el-radio-button>
              <el-radio-button label="1">周</el-radio-button>
              <el-radio-button label="2">月</el-radio-button>
            </el-radio-group>
          </div>
          <div class="report-duration"><svg-icon icon-class="info" />{{ durationTitle }}</div>
        </div>
      </el-col>
    </el-row>
    <el-row class="panel-group-report">
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <!-- title -->
          <div class="card-panel-description">
            <div class="card-panel-text">访客数</div>
            <count-to :start-val="0" :end-val="dateInfo.uvCount" :duration="3200" class="card-panel-num" />
          </div>
          <!-- contrast -->
          <div class="card-panel-contrast">
            <div class="card-panel-text">
              <div class="contrast-title">{{ contrastTitle }}</div>
              <div :class="[dateInfo.uvRate>0 ? 'up-style':'down-style','contrast-number']">
                <count-to :start-val="0" :end-val="dateInfo.uvRate | rateFormatter" :duration="3200" :decimals="2" suffix="%" class="contrast-number-left" />
                <div class="contrast-number-right"><i :class="dateInfo.uvRate>0 ? 'el-icon-top':'el-icon-bottom'" /></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">订单数量</div>
            <count-to :start-val="0" :end-val="dateInfo.orderCount" :duration="3200" class="card-panel-num" />
          </div>
          <!-- contrast -->
          <div class="card-panel-contrast">
            <div class="card-panel-text">
              <div class="contrast-title">{{ contrastTitle }}</div>
              <div :class="[dateInfo.orderRate>0 ? 'up-style':'down-style','contrast-number']">
                <count-to :start-val="0" :end-val="dateInfo.orderRate | rateFormatter" :duration="3200" :decimals="2" suffix="%" class="contrast-number-left" />
                <div class="contrast-number-right"><i :class="dateInfo.orderRate>0 ? 'el-icon-top':'el-icon-bottom'" /></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">支付金额</div>
            <count-to :start-val="0" :end-val="dateInfo.paymentAmount" :duration="3200" :decimals="2" class="card-panel-num" />
          </div>
          <!-- contrast -->
          <div class="card-panel-contrast">
            <div class="card-panel-text">
              <div class="contrast-title">{{ contrastTitle }}</div>
              <div :class="[dateInfo.paymentRate>0 ? 'up-style':'down-style','contrast-number']">
                <count-to :start-val="0" :end-val="dateInfo.paymentRate | rateFormatter" :duration="3200" :decimals="2" suffix="%" class="contrast-number-left" />
                <div class="contrast-number-right"><i :class="dateInfo.paymentRate>0 ? 'el-icon-top':'el-icon-bottom'" /></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">支付转化率</div>
            <count-to :start-val="0" :end-val="dateInfo.payConvertRate" :duration="3200" :decimals="2" suffix="%" class="card-panel-num" />
          </div>
          <!-- contrast -->
          <div class="card-panel-contrast">
            <div class="card-panel-text">
              <div class="contrast-title">{{ contrastTitle }}</div>
              <div :class="[dateInfo.payConvertRateRate>0 ? 'up-style':'down-style','contrast-number']">
                <count-to :start-val="0" :end-val="dateInfo.payConvertRateRate | rateFormatter" :duration="3200" :decimals="2" suffix="%" class="contrast-number-left" />
                <div class="contrast-number-right"><i :class="dateInfo.payConvertRateRate>0 ? 'el-icon-top':'el-icon-bottom'" /></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">客单价</div>
            <count-to :start-val="0" :end-val="dateInfo.prePrice" :duration="3200" :decimals="2" class="card-panel-num" />
          </div>
          <!-- contrast -->
          <div class="card-panel-contrast">
            <div class="card-panel-text">
              <div class="contrast-title">{{ contrastTitle }}</div>
              <div :class="[dateInfo.prePriceRate>0 ? 'up-style':'down-style','contrast-number']">
                <count-to :start-val="0" :end-val="dateInfo.prePriceRate | rateFormatter" :duration="3200" :decimals="2" suffix="%" class="contrast-number-left" />
                <div class="contrast-number-right"><i :class="dateInfo.prePriceRate>0 ? 'el-icon-top':'el-icon-bottom'" /></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">成功退款金额</div>
            <count-to :start-val="0" :end-val="dateInfo.refundAmount" :duration="3200" :decimals="2" class="card-panel-num" />
          </div>
          <!-- contrast -->
          <div class="card-panel-contrast">
            <div class="card-panel-text">
              <div class="contrast-title">{{ contrastTitle }}</div>
              <div :class="[dateInfo.refundRate>0 ? 'up-style':'down-style','contrast-number']">
                <count-to :start-val="0" :end-val="dateInfo.refundRate | rateFormatter" :duration="3200" :decimals="2" suffix="%" class="contrast-number-left" />
                <div class="contrast-number-right"><i :class="dateInfo.refundRate>0 ? 'el-icon-top':'el-icon-bottom'" /></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <!-- 图表 -->
    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <div class="app-container">
        <ve-line :extend="chartExtend" :data="chartData" :settings="chartSettings" />
      </div>
    </el-row>
  </div>
</template>

<script>
import { info } from '@/api/dashboard'
import { listAdsDateTopic, chartDuration } from '@/api/dw/report'
import VeLine from 'v-charts/lib/line'
import CountTo from 'vue-count-to'
import dateUtil from '@/utils/dateUtil'

const unitOptions = ['日', '周', '月']

export default {
  components: {
    CountTo,
    VeLine
  },
  filters: {
    rateFormatter: function(value) {
      return Math.abs(value)
    }
  },
  data() {
    return {
      userTotal: 0,
      goodsTotal: 0,
      productTotal: 0,
      orderTotal: 0,
      dateInfo: {

      },
      listQuery: {
        duration: 7,
        type: 0
      },
      chartData: {},
      chartSettings: {},
      chartExtend: {},
      contrastTitle: '较上日',
      durationTitle: '统计时间 2020-12-01 ~ 2020-12-09'
    }
  },
  created() {
    info().then(response => {
      this.userTotal = response.data.data.userTotal
      this.goodsTotal = response.data.data.goodsTotal
      this.productTotal = response.data.data.productTotal
      this.orderTotal = response.data.data.orderTotal
    })
    // 加载离线计算数据
    this.getOfflineData()
  },
  methods: {
    getOfflineData() {
      listAdsDateTopic(this.listQuery).then(response => {
        this.dateInfo = response.data.data
      })
      // 加载图表数据
      chartDuration(this.listQuery).then(response => {
        this.chartData = response.data.data
        this.chartSettings = {
          labelMap: {
            'orderCount': '订单量',
            'paymentCount': '支付量',
            'refundCount': '退款量',
            'newUserCount': '新增用户量'
          }
        }
        this.chartExtend = {
          xAxis: { boundaryGap: true }
        }
      })
    },
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    },
    handleFilter(type) {
      this.getOfflineData()
      // 更新title
      this.contrastTitle = '较上' + unitOptions[type]
      // 更新duration
      switch (type) {
        case '1':
          this.durationTitle = '统计时间 ' + dateUtil.getStartDayOfWeek() + '~' + dateUtil.getEndDayOfWeek()
          break
        case '2':
          this.durationTitle = '统计时间 ' + dateUtil.getStartDayOfMonth() + '~' + dateUtil.getEndDayOfMonth()
          break
        default:
          this.durationTitle = '统计时间 ' + dateUtil.getDurationDay(30) + '~' + dateUtil.getNowDay()
          break
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

.panel-group {
  margin-top: 18px;

  .card-panel-col{
    margin-bottom: 32px;
  }
  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);
    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }
      .icon-people {
         background: #40c9c6;
      }
      .icon-message {
        background: #36a3f7;
      }
      .icon-money {
        background: #f4516c;
      }
      .icon-shoppingCard {
        background: #34bfa3
      }
    }
    .icon-people {
      color: #40c9c6;
    }
    .icon-message {
      color: #36a3f7;
    }
    .icon-money {
      color: #f4516c;
    }
    .icon-shoppingCard {
      color: #34bfa3
    }
    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }
    .card-panel-icon {
      float: left;
      font-size: 48px;
    }
    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;
      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }
      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}

.panel-group-report {

  .card-panel-col{
    /*margin-bottom: 32px;*/
    border-style: solid;
    border-width: 1px;
    border-color: #dcdfe6;
  }
  .card-panel {
    height: 120px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);
    .card-panel-description {
      float: left;
      font-weight: bold;
      // margin: 14px 0 0 14px;
      padding: 16px 16px 10px 16px;
      transition: all 0.38s ease-out;
      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }
      .card-panel-num {
        font-size: 20px;
      }
    }
    .card-panel-contrast {
      float: left;
      width: 100%;
      padding: 10px;
      font-weight: bold;
      .card-panel-text {
        padding: 2px;
        line-height: 10px;
        font-size: 10px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 10px;
        .contrast-title{
          float: left;
          margin-left: 2%;
        }
        .contrast-number{
          float: right;
          margin-right: 25%;
          .contrast-number-left{
            float: left;
          }
          .contrast-number-right{
            float: left;
            margin-left: 5px;
          }
        }
        .up-style{
          color: red;
        }
        .down-style{
          color: green;
        }
      }
    }
  }
}

.panel-group-report-query{
    border-style: solid;
    border-width: 1px;
    border-color: #dcdfe6;
    .report-dec{
      height: 40px;
      font-size: 12px;
      position: relative;
      overflow: hidden;
      line-height: 40px;
      color: #666;
      background: #fff;
      box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
      border-color: rgba(0, 0, 0, .05);
      .report-title{
        float: left;
        font-size: 15px;
        font-weight: bold;
        margin-left: 20px;
      }
      .report-duration{
        float: right;
        position: relative;
        margin-right: 10px;
      }
      .report-param{
        float: right;
        margin-right: 20px;
      }
    }
}
</style>
