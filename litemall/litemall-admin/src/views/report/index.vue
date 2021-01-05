<template>
  <div class="page-container">
    <section class="main-left">
      <div class="left-top">
        <RealOrder />
      </div>
      <div class="left-bottom">
        <Funnel />
      </div>
    </section>
    <section class="main-center">
      <div class="center-top">
        <Map />
      </div>
      <div class="center-bottom">
        <OrderSpeed />
      </div>
    </section>
    <section class="main-right">
      <div class="right-top">
        <Sale />
      </div>
      <div class="right-bottom">右下</div>
    </section>
  </div>
</template>

<script>
import Map from './components/Map'
import Funnel from './components/Funnel'
import Sale from './components/Sale'
import OrderSpeed from './components/OrderSpeed'
import RealOrder from './components/RealOrder'

export default {
  components: {
    Map,
    Funnel,
    Sale,
    OrderSpeed,
    RealOrder
  },
  data() {
    return {}
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.page-container {
  width: 100%;
  height: 100vh; // 和屏幕高度一致
  display: flex;
  padding: 6px;

  & div {
    border: 1px solid black;
    text-align: center;
  }
  .main-left {
    height: 100%;
    width: 27.6%;
    .left-top {
      height: 46%;
      position: relative;
    }
    .left-bottom {
      height: 38%;
      margin-top: 25px;
      position: relative;
    }
  }
  .main-center {
    height: 100%;
    width: 41.5%;
    margin-left: 1.6%;
    margin-right: 1.6%;
    .center-top {
      width: 100%;
      height: 56%;
      position: relative;
    }
    .center-bottom {
      margin-top: 25px;
      width: 100%;
      height: 28%;
      position: relative;
    }
  }
  .main-right {
    height: 100%;
    width: 27.6%;
    .right-top {
      height: 46%;
      position: relative;
    }
    .right-bottom {
      height: 38%;
      margin-top: 25px;
      position: relative;
    }
  }
}
</style>
