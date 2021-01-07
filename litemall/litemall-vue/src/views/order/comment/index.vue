<template>
  <div class="comment-container">
    <!-- 商品列表 -->
    <div class="order-goods">
      <div v-for="item in order.orderGoods" :key="item.id" class="item">
        <div class="goods-title">
          <!-- image -->
          <a href="#" class="pic">
            <van-image :src="item.picUrl"/>
          </a>
          <div class="star">
            <!-- title -->
            <span class="star-title">{{ item.goodsName }}</span>
            <!-- star -->
            <div class="star-number">
              <van-rate v-model="star" class="star-rate" @change="changeItemStardec"/>
              <span class="star-dec">{{ starDesc }}</span>
            </div>
          </div>
        </div>
        <!-- 评论 -->
        <div class="goods-comment">
          <!-- 评论内容 -->
          <div class="comment-content">
            <van-field
              v-model="message"
              rows="3"
              autosize
              type="textarea"
              maxlength="500"
              placeholder="请写出您的感受，可以帮助更过小伙伴哦~"
              show-word-limit
            />
          </div>
          <!-- 图片 -->
          <div class="comment-pics">
            <van-uploader :after-read="afterRead" preview-size="40px"/>
            <span>晒出我的买家秀</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 物流 -->
    <div class="ship">
      <div class="ship-title">
        <van-icon name="logistics" /><span>物流服务评价</span>
      </div>
      <div class="ship-item">
        <span class="item-type">快递包装</span>
        <van-rate v-model="star" class="item-star"/>
      </div>
      <div class="ship-item">
        <span class="item-type">送货速度</span>
        <van-rate v-model="star" class="item-star"/>
      </div>
      <div class="ship-item">
        <span class="item-type">配送员服务</span>
        <van-rate v-model="star" class="item-star"/>
      </div>
    </div>

    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit" color="#ee0a24">提交</van-button>
    </div>
  </div>
</template>

<script>
import _ from 'lodash'

import { Image, Rate, Icon, Uploader, Field, Button } from 'vant'
import { orderDetail } from '@/api/api'

export default {
  components: {
    [Image.name]: Image,
    [Rate.name]: Rate,
    [Icon.name]: Icon,
    [Uploader.name]: Uploader,
    [Field.name]: Field,
    [Button.name]: Button
  },
  data() {
    return {
      order: {
        orderInfo: {},
        orderGoods: []
      },
      orderId: undefined,
      star: 2.5,
      starDesc: '非常好',
      message: ''
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
        console.info(this.order)
      })
    },
    afterRead(file) {
      // 此时可以自行将文件上传至服务器
      console.log(file)
    },
    changeItemStardec(value) {
      switch (value) {
        case 2:
          this.starDesc = '差'
          break
        case 3:
          this.starDesc = '一般'
          break
        case 4:
          this.starDesc = '好'
          break
        case 5:
          this.starDesc = '非常好'
          break
        default:
          this.starDesc = '非常差'
          break
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.comment-container {
  .order-goods {
    margin-bottom: 10px;
    .item {
      background-color: white;
      padding: 8px 16px;
      border-bottom-left-radius: 10px;
      border-bottom-right-radius: 10px;
      margin-bottom: 2px;
      .goods-title {
        display: flex;
        .pic {
          display: block;
          width: 88px;
          height: 88px;
          flex-grow: 0;
        }
        .star {
          flex-grow: 1;
          position: relative;
          .star-title {
            display: block;
            margin-top: 20px;
          }
          .star-number {
            position: absolute;
            bottom: 10px;
            display: flex;
            line-height: 20px;
            .star-dec {
              margin-left: 5px;
              font-size: 8px;
            }
          }
        }
      }
      .goods-comment {
        .comment-content {
        }
        .comment-pics {
          padding: 10px;
          text-align: center;
          border-radius: 5px;
          border: 1px dashed #c7c1c1;
          width: 120px;
          margin-top: 2px;
          background-color: rgba(66, 65, 65, 0.082);
          span {
            display: block;
            font-size: 6px;
          }
        }
      }
    }
  }

  .ship {
    background-color: white;
    border-radius: 10px;
    padding: 20px;
    color: #333;

    & div {
      margin-bottom: 10px;
    }

    .ship-title {
      span {
        margin-left: 5px;
      }
    }

    .ship-item {
      display: flex;
      justify-content: space-between;
      .item-type {
        flex-grow: 0;
        width: 100px;
        margin: 0 5px;
      }
      .item-star {
        flex-grow: 1;
      }
    }
  }
}
</style>
