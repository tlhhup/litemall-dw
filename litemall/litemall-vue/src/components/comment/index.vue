<template>
  <div class="comment-container">
    <!-- 评论列表 -->
    <div v-if="value.length>0" class="item-comment-list">
      <div v-for="item in value" :key="item.id" class="item-comment-warpper">
        <!-- 用户信息 -->
        <div class="item-comment-user">
          <div class="item-comment-user-avatar">
            <van-image :src="item.userInfo.avatarUrl" round/>
          </div>
          <div class="item-comment-user-star">
            <div class="item-comment-user-nickname">
              {{ item.userInfo.nickName }}
            </div>
            <div class="item-comment-user-rate">
              <van-rate v-model="item.star" readonly color="red" size="10"/>
            </div>
          </div>
        </div>
        <!-- 评论信息 -->
        <div class="item-comment-content">
          <div class="item-comment-content-message" @click="itemClick(item)">{{ item.content?item.content:'暂无评论' }}</div>
          <div class="item-comment-content-pics">
            <van-image v-for="picUrl in item.picList" :key="picUrl" :src="picUrl" @click="show=true"/>
            <van-image-preview v-model="show" :images="item.picList"/>
          </div>
        </div>
        <!-- sku信息 -->
        <div class="item-comment-sku" @click="itemClick(item)">{{ item.sku }}</div>
      </div>
    </div>
    <div v-else class="item-comment-empty">暂无评论</div>
  </div>
</template>

<script>
import { Image, Rate, ImagePreview } from 'vant'
export default {
  name: 'GoodsComment',
  components: {
    [Image.name]: Image,
    [Rate.name]: Rate,
    [ImagePreview.Component.name]: ImagePreview.Component
  },
  props: {
    value: {
      type: Array,
      default: function() {
        return []
      }
    }
  },
  data() {
    return {
      show: false
    }
  },
  methods: {
    itemClick(item) {
      // 发送事件，第一个参数为事件名，第二个为传递的参数
      this.$emit('item-click', item)
    }
  }
}
</script>

<style lang="scss" scoped>
.comment-container {
  .item-comment-list {
    background-color: white;
    .item-comment-warpper {
      background-color: white;
      padding: 2px;
      margin-bottom: 2px;
      box-shadow: 0 1px 2px rgba($color: #000000, $alpha: 0.03);
      // 用户信息
      .item-comment-user {
        height: 28px;
        display: flex;
        // 头像
        .item-comment-user-avatar {
          width: 28px;
        }
        .item-comment-user-star {
          margin-left: 5px;
          font-size: 12px;
          .item-comment-user-nickname {
          }
          .item-comment-user-rate {
          }
        }
      }
      .item-comment-content {
        margin-top: 5px;
        .item-comment-content-message {
        }
        .item-comment-content-pics {
          margin-top: 10px;
          height: 80px;
          overflow: scroll;
          .van-image {
            margin-right: 5px;
            float: left;
            width: 80px;
            height: 80px;
          }
        }
      }
      .item-comment-sku {
        font-size: 12px;
        color: gray;
      }
    }
  }
  .item-comment-empty {
    text-align: center;
  }
}
</style>
