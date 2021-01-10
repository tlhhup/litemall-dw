<template>
  <div class="item_detail">
    <van-swipe :autoplay="3000">
      <van-swipe-item v-for="(image, index) in goods.info.gallery" :key="index">
        <img v-lazy="image" width="100%">
      </van-swipe-item>
    </van-swipe>
    <van-cell-group v-if="goods" class="item_cell_group">
      <van-cell class="item_info">
        <div>
          <span class="item_price">{{ goods.info.retailPrice*100 | yuan }}</span>
          <span class="item_market_price">{{ goods.info.counterPrice*100 | yuan }}</span>
        </div>
        <div class="item-title">
          {{ goods.info.name }}
        </div>
        <div class="item_intro">{{ goods.info.brief }}</div>
      </van-cell>
    </van-cell-group>

    <div class="item_cell_group">
      <van-cell-group>
        <van-cell
          title="规格"
          is-link
          value="请选择"
          @click.native="skuClick"
        />
        <van-cell title="属性" is-link @click.native="propsPopup = true"/>
        <van-cell title="运费" value="满88免邮费"/>
      </van-cell-group>
      <van-sku
        v-model="showSku"
        :sku="sku"
        :hide-stock="true"
        :close-on-click-overlay="true"
        :goods="skuGoods"
        :goods-id="goods.info.id"
        @buy-clicked="buyGoods"
        @add-cart="addCart"
      />
      <van-popup v-model="propsPopup" position="bottom">
        <popup-props :props-str="props_str"/>
      </van-popup>
    </div>

    <div class="item-comment">
      <!-- 头部 -->
      <div class="item-comment-header">
        <span class="item-comment-header-line"/>
        <span class="item-comment-header-title">评价</span>
        <span class="item-comment-header-total">{{ comments.total }}</span>
      </div>
      <!-- 评论列表 -->
      <Comment v-model="comments.list" @item-click="itemClick"/>
      <!-- 列表按钮 -->
      <div v-show="comments.total>0" class="item-comment-list-page">
        <span @click="commentList">查看全部评价</span>
      </div>
    </div>

    <div class="item_desc">
      <div class="item_desc_title">商品详情</div>
      <div v-if="goods.info.detail" class="item_desc_wrap" v-html="goods.info.detail"/>
      <div v-else class="item_desc_wrap" style="text-align: center;">
        <p>无详情</p>
      </div>
    </div>

    <van-goods-action>
      <van-goods-action-icon :info="(cartInfo > 0) ? cartInfo : ''" icon="cart-o" @click="toCart"/>
      <van-goods-action-icon :style="(goods.userHasCollect !== 0) ? 'color: #f7b444;':''" icon="star-o" @click="addCollect"/>
      <van-goods-action-button type="warning" text="加入购物车" @click="skuClick"/>
      <van-goods-action-button type="danger" text="立即购买" @click="skuClick"/>
    </van-goods-action>

  </div>
</template>

<script>
import { addEvent } from '@/api/log'

import {
  goodsDetail,
  cartGoodsCount,
  collectAddOrDelete,
  cartAdd,
  cartFastAdd,
  commentList
} from '@/api/api'

import {
  Sku,
  Swipe,
  SwipeItem,
  GoodsAction,
  GoodsActionButton,
  GoodsActionIcon,
  Popup
} from 'vant'
import { setLocalStorage } from '@/utils/local-storage'
import popupProps from './popup-props'
import _ from 'lodash'
import Comment from '@/components/comment'

export default {
  components: {
    [Popup.name]: Popup,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
    [Sku.name]: Sku,
    [GoodsAction.name]: GoodsAction,
    [GoodsActionButton.name]: GoodsActionButton,
    [GoodsActionIcon.name]: GoodsActionIcon,
    [popupProps.name]: popupProps,
    Comment
  },
  props: {
    itemId: [String, Number]
  },

  data() {
    const isLogin = !!localStorage.getItem('Authorization')

    return {
      isLogin,
      goods: {
        userHasCollect: 0,
        info: {
          gallery: []
        }
      },
      sku: {
        tree: [],
        list: [],
        price: '1.00' // 默认价格（单位元）
      },
      skuGoods: {
        // 商品标题
        title: '',
        // 默认商品 sku 缩略图
        picture: ''
      },
      cartInfo: 0,
      selectSku: {
        selectedNum: 1,
        selectedSkuComb: {
          sku_str: 'aa'
        }
      },
      propsPopup: false,
      showSku: false,
      comments: [],
      show: false
    }
  },

  computed: {
    props_str() {
      const props_arr = []
      _.each(this.goods.attribute, json => {
        props_arr.push([json['attribute'], json['value']])
      })
      return props_arr || []
    }
  },

  created() {
    this.initData()
  },
  mounted() {
    // 记录商品展示
    addEvent({
      ett: new Date().getTime(),
      en: 'display',
      kv: {
        action: 2,
        goodsId: this.itemId,
        place: 1,
        extend1: 2,
        category: 1
      }
    })
  },

  methods: {
    skuClick() {
      this.showSku = true
    },
    initData() {
      goodsDetail({ id: this.itemId }).then(res => {
        this.goods = res.data.data
        this.skuAdapter()
      })

      cartGoodsCount().then(res => {
        this.cartInfo = res.data.data
      })

      // 获取评论数据
      const query = {
        type: 0,
        showType: 1,
        limit: 2,
        valueId: this.itemId
      }
      commentList(query).then(response => {
        this.comments = response.data.data
      })
    },
    toCart() {
      this.$router.push({
        name: 'cart'
      })
    },
    addCollect() {
      collectAddOrDelete({ valueId: this.itemId, type: 0 }).then(res => {
        if (this.goods.userHasCollect === 1) {
          this.goods.userHasCollect = 0
        } else {
          this.goods.userHasCollect = 1
          this.$toast({
            message: '收藏成功',
            duration: 1500
          })
        }
      })
      // 记录收藏
      if (this.goods.userHasCollect == 1) {
        addEvent({
          ett: new Date().getTime(),
          en: 'favorites',
          kv: {
            id: 0,
            courseId: this.itemId,
            userId: `${window.localStorage.getItem('userId') || ''}`,
            addTime: new Date().getTime()
          }
        })
      }
    },
    getProductId(s1, s2) {
      var productId
      var s1_name
      var s2_name
      _.each(this.goods.specificationList, specification => {
        _.each(specification.valueList, specValue => {
          if (specValue.id === s1) {
            s1_name = specValue.value
          } else if (specValue.id === s2) {
            s2_name = specValue.value
          }
        })
      })

      _.each(this.goods.productList, v => {
        const result = _.without(v.specifications, s1_name, s2_name)
        if (result.length === 0) {
          productId = v.id
        }
      })
      return productId
    },
    getProductIdByOne(s1) {
      var productId
      var s1_name
      _.each(this.goods.specificationList, specification => {
        _.each(specification.valueList, specValue => {
          if (specValue.id === s1) {
            s1_name = specValue.value
            return
          }
        })
      })

      _.each(this.goods.productList, v => {
        const result = _.without(v.specifications, s1_name)
        if (result.length === 0) {
          productId = v.id
        }
      })
      return productId
    },
    addCart(data) {
      const that = this
      const params = {
        goodsId: data.goodsId,
        number: data.selectedNum,
        productId: 0
      }
      if (_.has(data.selectedSkuComb, 's3')) {
        this.$toast({
          message: '目前仅支持两规格',
          duration: 1500
        })
        return
      } else if (_.has(data.selectedSkuComb, 's2')) {
        params.productId = this.getProductId(
          data.selectedSkuComb.s1,
          data.selectedSkuComb.s2
        )
      } else {
        params.productId = this.getProductIdByOne(data.selectedSkuComb.s1)
      }
      cartAdd(params).then(() => {
        this.cartInfo = this.cartInfo + data.selectedNum
        this.$toast({
          message: '已添加至购物车',
          duration: 1500
        })
        that.showSku = false
      })
      // 记录加购
      addEvent({
        ett: new Date().getTime(),
        en: 'addCar',
        kv: {
          userId: `${window.localStorage.getItem('userId') || ''}`,
          goodsId: params.goodsId,
          skuId: params.productId,
          num: params.number,
          addTime: new Date().getTime()
        }
      })
    },
    buyGoods(data) {
      const that = this
      const params = {
        goodsId: data.goodsId,
        number: data.selectedNum,
        productId: 0
      }
      if (_.has(data.selectedSkuComb, 's3')) {
        this.$toast({
          message: '目前仅支持两规格',
          duration: 1500
        })
        return
      } else if (_.has(data.selectedSkuComb, 's2')) {
        params.productId = this.getProductId(
          data.selectedSkuComb.s1,
          data.selectedSkuComb.s2
        )
      } else {
        params.productId = this.getProductIdByOne(data.selectedSkuComb.s1)
      }
      cartFastAdd(params).then(res => {
        const cartId = res.data.data
        setLocalStorage({ CartId: cartId })
        that.showSku = false
        this.$router.push('/order/checkout')
      })
    },
    skuAdapter() {
      const tree = this.setSkuTree()
      const list = this.setSkuList()
      const skuInfo = {
        price: parseInt(this.goods.info.retailPrice), // 未选择规格时的价格
        stock_num: 0, // TODO 总库存
        collection_id: '', // 无规格商品skuId取collection_id，否则取所选sku组合对应的id
        none_sku: false, // 是否无规格商品
        hide_stock: true
      }
      this.sku = {
        tree,
        list,
        ...skuInfo
      }
      this.skuGoods = {
        title: this.goods.info.name,
        picture: this.goods.info.picUrl
      }
    },
    setSkuList() {
      var sku_list = []
      _.each(this.goods.productList, v => {
        var sku_list_obj = {}
        _.each(v.specifications, (specificationName, index) => {
          sku_list_obj['s' + (~~index + 1)] = this.findSpecValueIdByName(
            specificationName
          )
        })

        sku_list_obj.price = v.price * 100
        sku_list_obj.stock_num = v.number
        sku_list.push(sku_list_obj)
      })

      return sku_list
    },
    findSpecValueIdByName(name) {
      let id = 0
      _.each(this.goods.specificationList, specification => {
        _.each(specification.valueList, specValue => {
          if (specValue.value === name) {
            id = specValue.id
            return
          }
        })
        if (id !== 0) {
          return
        }
      })
      return id
    },
    setSkuTree() {
      const that = this
      const specifications = []
      _.each(this.goods.specificationList, (v, k) => {
        const values = []
        _.each(v.valueList, vv => {
          vv.name = vv.value
          values.push({
            id: vv.id,
            name: vv.value,
            imUrl: vv.picUrl
          })
        })

        specifications.push({
          k: v.name,
          v: values,
          k_s: 's' + (~~k + 1)
        })
      })

      return specifications
    },
    commentList() {
      console.info('评论列表')
    },
    itemClick(item) {
      console.info(item)
    }
  }
}
</script>

<style lang="scss" scoped>
.item_detail {
  img {
    max-width: 100%;
  }
}

.item_cell_group {
  margin-bottom: 15px;
}

.item_price {
  font-size: 20px;
  color: $red;
  margin-right: 10px;
}

.item_market_price {
  color: $font-color-gray;
  text-decoration: line-through;
  font-size: $font-size-small;
}

.item-title {
  line-height: 1.4;
}

.item_dispatch {
  font-size: $font-size-small;
  color: $font-color-gray;
}

.item_intro {
  line-height: 18px;
  margin: 5px 0;
  font-size: $font-size-small;
  color: $font-color-gray;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.item_desc {
  background-color: #fff;
  /deep/ p {
    padding: 0 10px;
    margin-block-start: 0 !important;
    margin-block-end: 0 !important;
  }
  /deep/ img {
    max-width: 100%;
    display: block;
  }
}

.item_desc_title {
  @include one-border;
  padding: 10px 0;
  text-align: center;
}

.item-comment {
  background-color: #fff;
  padding: 10px;
  margin-bottom: 5px;

  .item-comment-header {
    height: 20px;
    line-height: 20px;
    margin-bottom: 2px;
    .item-comment-header-line {
      display: block;
      float: left;
      width: 3px;
      height: 17px;
      margin-right: 5px;
      background-image: linear-gradient(red, white);
    }
    .item-comment-header-title {
      font-weight: bold;
    }
    .item-comment-header-total {
      font-size: 12px;
      margin-left: 3px;
    }
  }

  // 评论列表按钮
  .item-comment-list-page {
    margin-top: 10px;
    text-align: center;
    span {
      display: block;
      margin: 0 auto;
      width: 100px;
      height: 30px;
      border: 1px solid gray;
      border-radius: 15px;
      font-size: 4px;
      padding: 5px 10px;
    }
  }
}
</style>
