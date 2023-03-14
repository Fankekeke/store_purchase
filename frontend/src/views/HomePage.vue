<template>
  <div :class="[multipage === true ? 'multi-page':'single-page', 'not-menu-page', 'home-page']">
    <a-row :gutter="8" class="head-info">
      <a-card class="head-info-card">
        <a-col :span="12">
          <div class="head-info-avatar">
            <img alt="头像" :src="avatar">
          </div>
          <div class="head-info-count">
            <div class="head-info-welcome">
              {{welcomeMessage}}
            </div>
            <div class="head-info-time">上次登录时间：{{user.lastLoginTime ? user.lastLoginTime : '第一次访问系统'}}</div>
          </div>
        </a-col>
        <a-col :span="12">
          <div>
            <a-row class="more-info">
              <a-col :span="4"></a-col>
              <a-col :span="4"></a-col>
              <a-col :span="4">
                <head-info title="本月入库数" :content="statisticsByMonth !== null ? statisticsByMonth.inTotal : 0" :center="false" :bordered="false"/>
              </a-col>
              <a-col :span="4">
                <head-info title="本月订单数" :content="statisticsByMonth !== null ? statisticsByMonth.orderTotal : 0" :center="false" :bordered="false"/>
              </a-col>
              <a-col :span="4">
                <head-info title="本月支出" :content="statisticsByMonth !== null ? (statisticsByMonth.inTotalPrice + statisticsByMonth.salaryTotalPrice) : 0" :center="false" />
              </a-col>
              <a-col :span="4">
                <head-info title="本月收入" :content="statisticsByMonth !== null ? statisticsByMonth.orderTotalPrice : 0" :center="false" />
              </a-col>
            </a-row>
          </div>
        </a-col>
      </a-card>
    </a-row>
    <a-row :gutter="8" class="count-info" style="margin-bottom: 8px">
      <a-col :span="24" class="visit-count-wrapper">
        <a-card class="visit-count">
          <a-skeleton active v-if="loading2" />
          <apexchart v-if="!loading2" type="area" height="300" :options="chartOptions2" :series="series2"></apexchart>
        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="8" class="count-info" style="margin-bottom: 8px">
      <a-col :span="8" class="visit-count-wrapper">
        <a-card class="visit-count">
          <a-skeleton active v-if="loading1" />
          <apexchart v-if="!loading1" type="donut" width="308" :options="chartOptions3" :series="series3"></apexchart>
        </a-card>
      </a-col>
      <a-col :span="16" class="visit-count-wrapper">
        <a-card class="visit-count">
          <a-skeleton active v-if="loading1" />
          <apexchart v-if="!loading1" type="line" height="300" :options="chartOptions1" :series="series1"></apexchart>
        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="8" class="count-info">
      <a-col :span="12" class="visit-count-wrapper">
        <a-card class="visit-count">
          <apexchart ref="count" type=bar height=300 :options="chartOptions" :series="series" />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script>
import HeadInfo from '@/views/common/HeadInfo'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'HomePage',
  components: {HeadInfo},
  data () {
    return {
      series3: [],
      chartOptions3: {
        chart: {
          type: 'donut'
        },
        labels: ['食品生鲜', '家用电器', '办公用品', '日常杂货'],
        responsive: [{
          breakpoint: 480,
          options: {
            chart: {
              width: 300
            },
            legend: {
              position: 'bottom'
            }
          }
        }]
      },
      series2: [{
        name: '入库',
        data: []
      }, {
        name: '出库',
        data: []
      }, {
        name: '订单',
        data: []
      }],
      chartOptions2: {
        chart: {
          height: 350,
          type: 'area'
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          curve: 'straight'
        },
        xaxis: {
          categories: []
        }
      },
      series1: [{
        data: []
      }],
      chartOptions1: {
        chart: {
          type: 'line',
          height: 350
        },
        xaxis: {
          categories: []
        },
        stroke: {
          curve: 'stepline'
        },
        dataLabels: {
          enabled: false
        },
        title: {
          text: '本月售出',
          align: 'left'
        },
        markers: {
          hover: {
            sizeOffset: 4
          }
        }
      },
      series: [],
      chartOptions: {
        chart: {
          toolbar: {
            show: false
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '35%'
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        fill: {
          opacity: 1

        }
      },
      todayIp: '',
      todayVisitCount: '',
      totalVisitCount: '',
      userRole: '',
      userDept: '',
      lastLoginTime: '',
      welcomeMessage: '',
      statisticsByMonth: null,
      materialTypeRate: null,
      lastSevenDaysCount: null,
      loading1: false,
      loading2: false,
      loading3: false
    }
  },
  computed: {
    ...mapState({
      multipage: state => state.setting.multipage,
      user: state => state.account.user
    }),
    avatar () {
      return `static/avatar/${this.user.avatar}`
    }
  },
  methods: {
    getStatisticsByMonth (year, month) {
      this.$get('/cos/order-info/statistics', {year, month}).then((r) => {
        this.statisticsByMonth = r.data
      })
    },
    getMaterialTypeRate (year, month) {
      this.loading1 = true
      this.$get('/cos/order-info/statistics/rate', {year, month}).then((r) => {
        this.materialTypeRate = r.data
        let series = []
        let series1 = []
        let chartOptions1 = []
        for (let i = 0; i < 4; i++) {
          let index = i + 1
          series.push(this.materialTypeRate[index + 'price'] ? this.materialTypeRate[index + 'price'] : 0)
        }
        if (this.materialTypeRate.materialSalesMapTop) {
          Object.keys(this.materialTypeRate.materialSalesMapTop).map(key => {
            series1.push(this.materialTypeRate.materialSalesMapTop[key])
            chartOptions1.push(key)
          })
        }
        setTimeout(() => {
          this.series3 = series
          this.series1[0].data = series1
          this.chartOptions1.xaxis.categories = chartOptions1
          this.loading1 = false
        }, 500)
      })
    },
    getLastSevenDaysCount () {
      this.loading2 = true
      this.$get('/cos/order-info/seven/count').then((r) => {
        this.lastSevenDaysCount = r.data
        let chartOptions = []
        let series1 = []
        let series2 = []
        let series3 = []
        this.lastSevenDaysCount.in.forEach(item => {
          chartOptions.push(item.days)
          series1.push(item.totalPrice ? item.totalPrice : 0)
        })
        this.lastSevenDaysCount.out.forEach(item => {
          series2.push(item.totalPrice ? item.totalPrice : 0)
        })
        this.lastSevenDaysCount.order.forEach(item => {
          series3.push(item.totalPrice ? item.totalPrice : 0)
        })
        setTimeout(() => {
          this.series2[0].data = series1
          this.series2[1].data = series2
          this.series2[2].data = series3
          this.chartOptions2.xaxis.categories = chartOptions
          this.loading2 = false
        }, 500)
      })
    },
    welcome () {
      const date = new Date()
      const hour = date.getHours()
      let time = hour < 6 ? '早上好' : (hour <= 11 ? '上午好' : (hour <= 13 ? '中午好' : (hour <= 18 ? '下午好' : '晚上好')))
      return `${time}，${this.user.username}`
    }
  },
  mounted () {
    this.welcomeMessage = this.welcome()
    this.$get(`index/${this.user.username}`).then((r) => {
      let data = r.data.data
      this.todayIp = data.todayIp
      this.todayVisitCount = data.todayVisitCount
      this.totalVisitCount = data.totalVisitCount
      let sevenVisitCount = []
      let dateArr = []
      for (let i = 6; i >= 0; i--) {
        let time = moment().subtract(i, 'days').format('MM-DD')
        let contain = false
        for (let o of data.lastSevenVisitCount) {
          if (o.days === time) {
            contain = true
            sevenVisitCount.push(o.count)
          }
        }
        if (!contain) {
          sevenVisitCount.push(0)
        }
        dateArr.push(time)
      }
      let sevenUserVistCount = []
      for (let i = 6; i >= 0; i--) {
        let time = moment().subtract(i, 'days').format('MM-DD')
        let contain = false
        for (let o of data.lastSevenUserVisitCount) {
          if (o.days === time) {
            contain = true
            sevenUserVistCount.push(o.count)
          }
        }
        if (!contain) {
          sevenUserVistCount.push(0)
        }
      }
      this.$refs.count.updateSeries([
        {
          name: '您',
          data: sevenUserVistCount
        },
        {
          name: '总数',
          data: sevenVisitCount
        }
      ], true)
      this.$refs.count.updateOptions({
        xaxis: {
          categories: dateArr
        },
        title: {
          text: '近七日系统访问记录',
          align: 'left'
        }
      }, true, true)
    }).catch((r) => {
      console.error(r)
      this.$message.error('获取首页信息失败')
    })
    let myDate = new Date()
    let year = myDate.getFullYear()
    let month = myDate.getMonth()
    this.getStatisticsByMonth(year, month + 1)
    this.getMaterialTypeRate(year, month + 1)
    this.getLastSevenDaysCount()
  }
}
</script>
<style lang="less">
  .home-page {
    .head-info {
      margin-bottom: .5rem;
      .head-info-card {
        padding: .5rem;
        border-color: #f1f1f1;
        .head-info-avatar {
          display: inline-block;
          float: left;
          margin-right: 1rem;
          img {
            width: 5rem;
            border-radius: 2px;
          }
        }
        .head-info-count {
          display: inline-block;
          float: left;
          .head-info-welcome {
            font-size: 1.05rem;
            margin-bottom: .1rem;
          }
          .head-info-desc {
            color: rgba(0, 0, 0, 0.45);
            font-size: .8rem;
            padding: .2rem 0;
            p {
              margin-bottom: 0;
            }
          }
          .head-info-time {
            color: rgba(0, 0, 0, 0.45);
            font-size: .8rem;
            padding: .2rem 0;
          }
        }
      }
    }
    .count-info {
      .visit-count-wrapper {
        padding-left: 0 !important;
        .visit-count {
          padding: .5rem;
          border-color: #f1f1f1;
          .ant-card-body {
            padding: .5rem 1rem !important;
          }
        }
      }
      .project-wrapper {
        padding-right: 0 !important;
        .project-card {
          border: none !important;
          .ant-card-head {
            border-left: 1px solid #f1f1f1 !important;
            border-top: 1px solid #f1f1f1 !important;
            border-right: 1px solid #f1f1f1 !important;
          }
          .ant-card-body {
            padding: 0 !important;
            table {
              width: 100%;
              td {
                width: 50%;
                border: 1px solid #f1f1f1;
                padding: .6rem;
                .project-avatar-wrapper {
                  display:inline-block;
                  float:left;
                  margin-right:.7rem;
                  .project-avatar {
                    color: #42b983;
                    background-color: #d6f8b8;
                  }
                }
              }
            }
          }
          .project-detail {
            display:inline-block;
            float:left;
            text-align:left;
            width: 78%;
            .project-name {
              font-size:.9rem;
              margin-top:-2px;
              font-weight:600;
            }
            .project-desc {
              color:rgba(0, 0, 0, 0.45);
              p {
                margin-bottom:0;
                font-size:.6rem;
                white-space:normal;
              }
            }
          }
        }
      }
    }
  }
</style>
