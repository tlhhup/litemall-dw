<template>
  <div class="page-container">
    <section class="main-left">
      <div class="left-top">左上</div>
      <div class="left-bottom" />
    </section>
    <section class="main-center">
      <div class="center-top">
        <Map />
      </div>
      <div class="center-bottom">中下</div>
    </section>
    <section class="main-right">
      <div class="right-top">右上</div>
      <div class="right-bottom">右下</div>
    </section>
  </div>
</template>

<script>
import Map from './components/Map'

export default {
  components: {
    Map
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
      height: 53%;
      position: relative;
    }
    .left-bottom {
      height: 31%;
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
