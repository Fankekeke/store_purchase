<template>
  <a-modal v-model="show" title="新增员工" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        提交
      </a-button>
    </template>
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="16">
          <a-form-item label='选择员工' v-bind="formItemLayout">
            <a-select style="width: 100%" v-model="staffInfo" @change="staffChange" option-label-prop="label">
              <a-select-option v-for="(item, index) in staffList" :key="index" :value="item.id" :label="item.staffName">
                <a-row>
                  <a-col :span="4">
                    <a-avatar style="margin-right: 20px" shape="square" :size="40" icon="user" :src="'http://127.0.0.1:9527/imagesWeb/' + item.avatar" />
                  </a-col>
                  <a-col :span="20">
                    <a-row>
                      <a-col><span>{{item.staffName}}</span></a-col>
                      <a-col style="font-size: 10px">
                        <span v-if="item.staffType === 1">售货员</span>
                        <span v-if="item.staffType === 2">理货员</span>
                        <span v-if="item.staffType === 3">收银员</span>
                        <span v-if="item.staffType === 4">分拣员</span>
                        <span v-if="item.staffType === 5">杂工</span>
                      </a-col>
                    </a-row>
                  </a-col>
                </a-row>
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="试用薪资">
            <a-input-number style="width: 100%" v-decorator="[
              'salary', { rules: [{ required: true, message: '请填写试用薪资!' }] }
              ]"
            />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="备注">
            <a-textarea placeholder="Basic usage" :rows="4" v-decorator="['remark']"/>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import {mapState} from 'vuex'
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'salaryGainAdd',
  props: {
    salaryGainAddVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.salaryGainAddVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      staffList: [],
      staffInfo: null,
      staffCode: '',
      gain: 0
    }
  },
  mounted () {
    this.getStaffList()
  },
  watch: {
    staffCode: function (value) {
      if (this.staffCode) {
        this.getGainByStaffCode(this.staffCode)
      }
    }
  },
  methods: {
    staffChange (value) {
      this.staffList.forEach(item => {
        if (item.id === value) {
          this.staffCode = item.staffCode
        }
      })
    },
    getStaffList () {
      this.$get('/cos/staff-info/list').then((r) => {
        this.staffList = r.data.data
      })
    },
    getGainByStaffCode (staffCode) {
      this.$get(`/cos/salary-gain/gain/${staffCode}`).then((r) => {
        this.gain = r.data.data
        this.form.setFieldsValue({'salary': this.gain})
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
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      if (!this.staffInfo) {
        this.$message.error('请选择员工！')
        return false
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          values.staffCode = this.staffCode
          this.$post('/cos/salary-gain', {
            ...values
          }).then((r) => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
