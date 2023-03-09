<template>
  <a-modal v-model="show" title="修改工作人员" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        修改
      </a-button>
    </template>
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="8">
          <a-form-item label='员工姓名' v-bind="formItemLayout">
            <a-input v-decorator="[
            'staffName',
            { rules: [{ required: true, message: '请输入员工姓名!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='联系方式' v-bind="formItemLayout">
            <a-input v-decorator="[
            'email',
            { rules: [{ required: true, message: '请输入联系方式!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='性别' v-bind="formItemLayout">
            <a-select v-decorator="[
              'staffSex',
              { rules: [{ required: true, message: '请输入性别!' }] }
              ]">
              <a-select-option value="1">男</a-select-option>
              <a-select-option value="2">女</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='员工类型' v-bind="formItemLayout">
            <a-select v-decorator="[
              'staffType',
              { rules: [{ required: true, message: '请输入员工类型!' }] }
              ]">
              <a-select-option value="1">售货员</a-select-option>
              <a-select-option value="2">理货员</a-select-option>
              <a-select-option value="3">收银员</a-select-option>
              <a-select-option value="4">分拣员</a-select-option>
              <a-select-option value="5">杂工</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="出生日期">
            <a-date-picker style="width: 100%" v-decorator="['birthDate',{rules: [{ required: true, message: '出生日期' }]}]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="备注">
            <a-textarea placeholder="Basic usage" :rows="4" v-decorator="['remark']"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='员工照片' v-bind="formItemLayout">
            <a-upload
              name="avatar"
              action="http://127.0.0.1:9527/file/fileUpload/"
              list-type="picture-card"
              :file-list="fileList"
              @preview="handlePreview"
              @change="picHandleChange"
            >
              <div v-if="fileList.length < 1">
                <a-icon type="plus" />
                <div class="ant-upload-text">
                  Upload
                </div>
              </div>
            </a-upload>
            <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
              <img alt="example" style="width: 100%" :src="previewImage" />
            </a-modal>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import {mapState} from 'vuex'
import moment from 'moment'
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
  name: 'StaffEdit',
  props: {
    staffEditVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.staffEditVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      rowId: null,
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: ''
    }
  },
  methods: {
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
    setFormValues ({...staff}) {
      this.rowId = staff.id
      let fields = ['staffName', 'email', 'staffSex', 'staffType', 'remark']
      let obj = {}
      Object.keys(staff).forEach((key) => {
        if (key === 'avatar') {
          this.fileList = []
          this.imagesInit(staff['avatar'])
        }
        if (key === 'staffSex') {
          staff[key] = staff[key].toString()
        }
        if (key === 'staffType') {
          staff[key] = staff[key].toString()
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = staff[key]
        }
      })
      this.form.setFieldsValue(obj)
      setTimeout(() => {
        if (staff['birthDate']) {
          this.form.setFieldsValue({'birthDate': moment(staff['birthDate'])})
        }
      }, 200)
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
      // 获取图片List
      let images = []
      this.fileList.forEach(image => {
        images.push(image.name)
      })
      this.form.validateFields((err, values) => {
        values.id = this.rowId
        values.avatar = images.length > 0 ? images.join(',') : null
        if (values.birthDate) {
          values.birthDate = moment(values.birthDate).format('YYYY-MM-DD')
        }
        if (!err) {
          this.loading = true
          this.$put('/cos/staff-info', {
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
