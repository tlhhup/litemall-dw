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
    <el-row class="panel-group-report">
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">访客数</div>
            <count-to :start-val="0" :end-val="dateInfo.uvCount" :duration="3200" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">订单数量</div>
            <count-to :start-val="0" :end-val="dateInfo.orderCount" :duration="3200" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">支付金额</div>
            <count-to :start-val="0" :end-val="dateInfo.paymentAmount" :duration="3200" :decimals="2" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">支付转化率</div>
            <count-to :start-val="0" :end-val="dateInfo.payConvertRate" :duration="3200" :decimals="2" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">客单价</div>
            <count-to :start-val="0" :end-val="dateInfo.prePrice" :duration="3200" :decimals="2" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="5" :lg="4" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-description">
            <div class="card-panel-text">成功退款金额</div>
            <count-to :start-val="0" :end-val="dateInfo.refundAmount" :duration="3200" :decimals="2" class="card-panel-num" />
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

export default {
  components: {
    CountTo,
    VeLine
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
        date: undefined,
        type: 0
      },
      duration: 7,
      chartData: {},
      chartSettings: {},
      chartExtend: {}
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
    listAdsDateTopic(this.listQuery).then(response => {
      this.dateInfo = response.data.data
    })
    // 加载图表数据
    chartDuration(this.duration).then(response => {
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
  methods: {
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
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
    height: 108px;
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
      margin: 14px 0 0 14px;
      padding: 16px;
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
  }
}
</style>
