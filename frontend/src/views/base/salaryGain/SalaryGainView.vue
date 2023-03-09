<template>
  <a-modal v-model="show" title="员工薪资调幅记录" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="salaryGainData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">薪资信息</span></a-col>
        <a-col :span="8"><b>薪资调幅：</b>
          {{ salaryGainData.salary !== null ? salaryGainData.salary : '- -' }}
        </a-col>
        <a-col :span="8"><b>更新时间：</b>
          {{ salaryGainData.createDate !== null ? salaryGainData.createDate : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">备 注</span></a-col>
        <a-col :span="24">
          {{ salaryGainData.remark !== null ? salaryGainData.remark : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">员工信息</span></a-col>
        <a-col :span="8"><b>员工姓名：</b>
          <a-popover>
            <template slot="content">
              <a-avatar v-if="salaryGainData.avatar !== null" shape="square" :size="132" icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + salaryGainData.avatar" />
              <a-avatar v-else shape="square" :size="132" icon="user" />
            </template>
            <a>{{ salaryGainData.staffName !== null ? salaryGainData.staffName : '- -' }}</a>
          </a-popover>
        </a-col>
        <a-col :span="8"><b>员工职务：</b>
          <span v-if="salaryGainData.staffType === 1">售货员</span>
          <span v-if="salaryGainData.staffType === 2">理货员</span>
          <span v-if="salaryGainData.staffType === 3">收银员</span>
          <span v-if="salaryGainData.staffType === 4">分拣员</span>
          <span v-if="salaryGainData.staffType === 5">杂工</span>
        </a-col>
      </a-row>
      <br/>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">薪资涨幅</span></a-col>
        <a-col :span="24">
          <a-skeleton active v-if="chartLoading" />
          <apexchart v-if="!chartLoading" type="area" height="300" :options="chartOptions1" :series="series1"></apexchart>
        </a-col>
      </a-row>
      <br/>
    </div>
  </a-modal>
</template>

<script>
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'salaryGainView',
  props: {
    salaryGainShow: {
      type: Boolean,
      default: false
    },
    salaryGainData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.salaryGainShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      chartLoading: false,
      gainList: [],
      series1: [{
        name: '薪资调幅',
        data: [31, 40, 28, 51, 42, 109, 100, 100, 100, 100, 100, 100]
      }],
      chartOptions1: {
        chart: {
          height: 350,
          type: 'area'
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          curve: 'smooth'
        },
        xaxis: {
          categories: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
        }
      }
    }
  },
  watch: {
    salaryGainShow: function (value) {
      if (this.salaryGainData) {
        this.getGainListByStaff(this.salaryGainData.staffCode)
      }
    }
  },
  methods: {
    getGainListByStaff (staffCode) {
      this.chartLoading = true
      this.$get(`/cos/salary-gain/${staffCode}`).then((r) => {
        this.gainList = r.data.record
        let series = []
        let chartOptions = []
        this.gainList.forEach(item => {
          series.push(item.salary)
          chartOptions.push(item.createDate)
        })
        this.series1[0].data = series
        this.chartOptions1.xaxis.categories = chartOptions
        setTimeout(() => {
          this.chartLoading = false
        }, 200)
      })
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
</style>
