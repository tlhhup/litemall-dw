<template>
  <div class="comment-container">
    <!-- 商品列表 -->
    <div class="order-goods">
      <div v-for="item in orderGoods" :key="item.id" class="item">
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
              <van-rate v-model="item.star" class="star-rate" color="red" @change="changeItemStardec(item,arguments)"/>
              <span class="star-dec">{{ item.starDesc ? item.starDesc:'非常好' }}</span>
            </div>
          </div>
        </div>
        <!-- 评论 -->
        <div class="goods-comment">
          <!-- 评论内容 -->
          <div class="comment-content">
            <van-field
              v-model="item.message"
              rows="3"
              type="textarea"
              maxlength="200"
              placeholder="请写出您的感受，可以帮助更过小伙伴哦~"
              show-word-limit
              clearable
            />
          </div>
          <!-- 图片 -->
          <div class="comment-pics">
            <van-uploader
              v-model="item.commentPics"
              :max-count="3"
              :after-read="afterRead"
              upload-text="拍照"
              multiple/>
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
        <van-rate v-model="shipStar" class="item-star" color="red"/>
      </div>
      <div class="ship-item">
        <span class="item-type">送货速度</span>
        <van-rate v-model="shipSpeedStar" class="item-star" color="red"/>
      </div>
      <div class="ship-item">
        <span class="item-type">配送员服务</span>
        <van-rate v-model="shiperStar" class="item-star" color="red"/>
      </div>
    </div>

    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit" color="#ee0a24" @click="submitComment">提交</van-button>
    </div>
  </div>
</template>

<script>
import _ from 'lodash'

import { Image, Rate, Icon, Uploader, Field, Button, Dialog } from 'vant'
import { orderGoods, orderComment, uploadImage } from '@/api/api'

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
      orderGoods: [],
      orderId: undefined,
      shipStar: 0,
      shipSpeedStar: 0,
      shiperStar: 0
    }
  },
  created() {
    if (_.has(this.$route.params, 'orderId')) {
      this.orderId = this.$route.params.orderId
      this.getOrderGoods(this.orderId)
    }
  },
  methods: {
    getOrderGoods(orderId) {
      orderGoods({ orderId: orderId }).then(res => {
        this.orderGoods = res.data.data
      })
    },
    changeItemStardec(item, arg) {
      switch (arg[0]) {
        case 2:
          item.starDesc = '差'
          break
        case 3:
          item.starDesc = '一般'
          break
        case 4:
          item.starDesc = '好'
          break
        case 5:
          item.starDesc = '非常好'
          break
        default:
          item.starDesc = '非常差'
          break
      }
    },
    afterRead(file) {
      // 此时可以自行将文件上传至服务器
      const param = new FormData()
      param.append('file', file.file)
      uploadImage(param).then(response => {
        const { data: ret } = response.data
        // 更新URL地址
        file.url = ret.url
      })
    },
    submitComment() {
      const postData = {
        orderGoods: this.orderGoods,
        shipStar: this.shipStar,
        shipSpeedStar: this.shipSpeedStar,
        shiperStar: this.shiperStar,
        orderId: this.orderId
      }
      orderComment(postData).then(response => {
        Dialog.alert({
          message: '评论发表成功'
        }).then(() => {
          this.$router.back(-1)
        })
      })
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
            }
          }
        }
      }
      .goods-comment {
        .comment-content {
          box-shadow: 0 0 2px rgba($color: #000000, $alpha: 0.08);
        }
        .comment-pics {
          padding: 8px;
          padding-bottom: 0;
          border-radius: 5px;
          border: 1px dashed #c7c1c1;
          margin-top: 2px;
          background-color: rgba(66, 65, 65, 0.082);
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
