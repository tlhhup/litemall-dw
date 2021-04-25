<template>
  <div class="com-container">
    <div ref="tag_ref" class="com-chart" />
  </div>
</template>

<script>
import echarts from 'echarts'

export default {
  props: {
    tags: {
      type: Object,
      require: false,
      default: undefined
    }
  },
  data() {
    return {
      chart: undefined
    }
  },
  watch: {
    tags: {
      deep: true,
      immediate: true,
      handler(val) {
        if (val !== undefined) {
          this.updateChart(val)
        }
      }
    }
  },
  mounted() {
    this.initChart()
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.tag_ref)
      const option = {
        series: [
          {
            type: 'graph',
            layout: 'force',
            animation: false,
            draggable: true,
            roam: true,
            label: {
              show: true,
              position: 'right'
            },
            force: {
              repulsion: 200,
              edgeLength: 50
            }
          }
        ]
      }

      this.chart.setOption(option)
    },
    updateChart(tags) {
      const option = {
        series: [
          {
            data: tags.nodes,
            links: tags.links
          }
        ]
      }
      this.chart.setOption(option)
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.com-container {
  .com-chart {
    width: 100%;
    height: 80vh;
  }
}
</style>
