<template>
  <a-modal v-model="show" title="员工信息详情" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="staffData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">员工信息</span></a-col>
        <a-col :span="8"><b>员工姓名：</b>
          <a-popover>
            <template slot="content">
              <a-avatar v-if="staffData.avatar !== null" shape="square" :size="132" icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + staffData.avatar" />
              <a-avatar v-else shape="square" :size="132" icon="user" />
            </template>
            <a>{{ staffData.staffName !== null ? staffData.staffName : '- -' }}</a>
          </a-popover>
        </a-col>
        <a-col :span="8"><b>联系方式：</b>
          <a-tooltip>
            <template slot="title">
              {{ staffData.email }}
            </template>
            {{ staffData.email.slice(0, 8) }} ...
          </a-tooltip>
        </a-col>
        <a-col :span="8"><b>性别：</b>
          <span v-if="staffData.staffSex === 1">男</span>
          <span v-if="staffData.staffSex === 2">女</span>
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>员工职务：</b>
          <span v-if="staffData.staffType === 1">售货员</span>
          <span v-if="staffData.staffType === 2">理货员</span>
          <span v-if="staffData.staffType === 3">收银员</span>
          <span v-if="staffData.staffType === 4">分拣员</span>
          <span v-if="staffData.staffType === 5">杂工</span>
        </a-col>
        <a-col :span="8"><b>出生日期：</b>
          {{ staffData.birthDate }}
        </a-col>
      </a-row>
      <br/>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">当前薪资</span></a-col>
        <a-col :span="24">
          {{ gain }} 元
        </a-col>
      </a-row>
      <br/>
    </div>
  </a-modal>
</template>

<script>
import moment from 'moment'
moment.locale('zh-cn')
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'StaffView',
  props: {
    staffShow: {
      type: Boolean,
      default: false
    },
    staffData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.staffShow
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
      gain: 0
    }
  },
  watch: {
    staffShow: function (value) {
      if (value && this.staffData.avatar !== null && this.staffData.avatar !== '') {
        this.getGainByStaffCode(this.staffData.staffCode)
        this.imagesInit(this.staffData.avatar)
      }
    }
  },
  methods: {
    handleCancel () {
      this.previewVisible = false
    },
    getGainByStaffCode (staffCode) {
      this.$get(`/cos/salary-gain/gain/${staffCode}`).then((r) => {
        this.gain = r.data.data
      })
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
