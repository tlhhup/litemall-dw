<template>
  <div class="payment">
    <div class="time_down payment_group">
      请在
      <span class="red">半小时内</span>
      完成付款，否则系统自动取消订单
    </div>

    <van-cell-group class="payment_group">
      <van-cell :value="order.orderInfo.orderSn" title="订单编号"/>
      <van-cell title="实付金额">
        <span class="red">{{ order.orderInfo.actualPrice *100 | yuan }}</span>
      </van-cell>
    </van-cell-group>

    <div class="pay_way_group">
      <div class="pay_way_title">选择支付方式</div>
      <van-radio-group v-model="payWay">
        <van-cell-group>
          <van-cell>
            <template slot="title">
              <img src="../../../assets/images/ali_pay.png" alt="支付宝" width="82" height="29">
            </template>
            <van-radio name="ali"/>
          </van-cell>
          <van-cell>
            <template slot="title">
              <img src="../../../assets/images/wx_pay.png" alt="微信支付" width="113" height="23">
            </template>
            <van-radio name="wx"/>
          </van-cell>
        </van-cell-group>
      </van-radio-group>
    </div>

    <van-button class="pay_submit" type="primary" bottom-action @click="pay">去支付</van-button>
  </div>
</template>

<script>
import { Radio, RadioGroup, Dialog } from 'vant'
import { orderDetail, orderPrepay, orderH5pay, simulatePay } from '@/api/api'
import _ from 'lodash'
import { getLocalStorage, setLocalStorage } from '@/utils/local-storage'

export default {
  name: 'Payment',

  components: {
    [Radio.name]: Radio,
    [RadioGroup.name]: RadioGroup,
    [Dialog.name]: Dialog
  },

  data() {
    return {
      payWay: 'wx',
      order: {
        orderInfo: {},
        orderGoods: []
      },
      orderId: 0
    }
  },
  created() {
    if (_.has(this.$route.params, 'orderId')) {
      this.orderId = this.$route.params.orderId
      this.getOrder(this.orderId)
    }
  },
  methods: {
    getOrder(orderId) {
      orderDetail({ orderId: orderId }).then(res => {
        this.order = res.data.data
      })
    },
    pay() {
      Dialog.alert({
        message: '你选择了' + (this.payWay === 'wx' ? '微信支付' : '支付宝支付')
      }).then(() => {
        if (this.payWay === 'wx') {
          const ua = navigator.userAgent.toLowerCase()
          const isWeixin = ua.indexOf('micromessenger') != -1
          if (isWeixin) {
            orderPrepay({ orderId: this.orderId })
              .then(res => {
                const data = res.data.data
                const prepay_data = JSON.stringify({
                  appId: data.appId,
                  timeStamp: data.timeStamp,
                  nonceStr: data.nonceStr,
                  package: data.packageValue,
                  signType: 'MD5',
                  paySign: data.paySign
                })
                setLocalStorage({ prepay_data: prepay_data })

                if (typeof WeixinJSBridge === 'undefined') {
                  if (document.addEventListener) {
                    document.addEventListener(
                      'WeixinJSBridgeReady',
                      this.onBridgeReady,
                      false
                    )
                  } else if (document.attachEvent) {
                    document.attachEvent(
                      'WeixinJSBridgeReady',
                      this.onBridgeReady
                    )
                    document.attachEvent(
                      'onWeixinJSBridgeReady',
                      this.onBridgeReady
                    )
                  }
                } else {
                  this.onBridgeReady()
                }
              })
              .catch(err => {
                Dialog.alert({ message: err.data.errmsg })
                that.$router.replace({
                  name: 'paymentStatus',
                  params: {
                    status: 'failed'
                  }
                })
              })
          } else {
            /**
            orderH5pay({ orderId: this.orderId })
              .then(res => {
                let data = res.data.data;
                window.location.replace(
                  data.mwebUrl +
                  '&redirect_url=' +
                  encodeURIComponent(
                    window.location.origin +
                    '/#/?orderId=' +
                    this.orderId +
                    '&tip=yes'
                  )
                );
              })
              .catch(err => {
                Dialog.alert({ message: err.data.errmsg });
              });
            */
            simulatePay({ orderId: this.orderId })
              .then(res => {
                if (res.data.errno == 0) {
                  // 跳转到支付状态页面
                  this.$router.replace({
                    name: 'paymentStatus',
                    params: {
                      status: 'success'
                    }
                  })
                }
              })
              .catch(err => {
                Dialog.alert({ message: err.data.errmsg })
              })
          }
        } else {
          // todo : alipay
        }
      })
    },
    onBridgeReady() {
      const that = this
      const data = getLocalStorage('prepay_data')
      // eslint-disable-next-line no-undef
      WeixinJSBridge.invoke(
        'getBrandWCPayRequest',
        JSON.parse(data.prepay_data),
        function(res) {
          if (res.err_msg == 'get_brand_wcpay_request:ok') {
            that.$router.replace({
              name: 'paymentStatus',
              params: {
                status: 'success'
              }
            })
          } else if (res.err_msg == 'get_brand_wcpay_request:cancel') {
            that.$router.replace({
              name: 'paymentStatus',
              params: {
                status: 'cancel'
              }
            })
          } else {
            that.$router.replace({
              name: 'paymentStatus',
              params: {
                status: 'failed'
              }
            })
          }
        }
      )
    }
  }
}
</script>

<style lang="scss" scoped>
.payment_group {
  margin-bottom: 10px;
}

.time_down {
  background-color: #fffeec;
  padding: 10px 15px;
}

.pay_submit {
  position: fixed;
  bottom: 0;
  width: 100%;
}

.pay_way_group img {
  vertical-align: middle;
}

.pay_way_title {
  padding: 15px;
  background-color: #fff;
}
</style>
