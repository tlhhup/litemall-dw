<template>
  <div class="com-container">
    <div class="com-chart">
      <div class="item">
        <span class="card-panel-text">订单数</span>
        <count-to :start-val="0" :end-val="real.orderCount" class="card-panel-num" />
      </div>
      <div class="item">
        <span class="card-panel-text">订单金额</span>
        <count-to :start-val="0" :end-val="real.orderAmount" :decimals="2" class="card-panel-num" />
      </div>
      <div class="item">
        <span class="card-panel-text">支付数</span>
        <count-to :start-val="0" :end-val="real.payCount" class="card-panel-num" />
      </div>
      <div class="item">
        <span class="card-panel-text">支付金额</span>
        <count-to :start-val="0" :end-val="real.payAmount" :decimals="2" class="card-panel-num" />
      </div>
    </div>
  </div>
</template>

<script>
import { realTime } from '@/api/dw/report'
import CountTo from 'vue-count-to'
export default {
  components: {
    CountTo
  },
  data() {
    return {
      real: undefined,
      timer: undefined
    }
  },
  mounted() {
    this.loadData()
    this.startInteval()
  },
  destroyed() {
    clearInterval(this.timer)
  },
  methods: {
    loadData() {
      realTime().then(response => {
        const { data: ret } = response.data
        this.real = ret
      })
    },
    startInteval() {
      this.timer = setInterval(() => {
        this.loadData()
      }, 10 * 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
.com-chart {
  background-color: #222733;

  .item {
    font-weight: bold;
    margin: 26px;
    margin-left: 0px;
    color: white;
    .card-panel-text {
      line-height: 18px;
      font-size: 16px;
      margin-bottom: 12px;
    }
    .card-panel-num {
      font-size: 20px;
    }
  }
}
</style>
