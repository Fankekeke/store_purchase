<template>
  <a-modal v-model="show" title="修改供应商" @cancel="onClose" :width="800">
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
          <a-form-item label='供应商名称' v-bind="formItemLayout">
            <a-input v-decorator="[
            'supplierName',
            { rules: [{ required: true, message: '请输入供应商名称!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='可供采购类型' v-bind="formItemLayout">
            <a-select v-decorator="[
              'purchaseType',
              { rules: [{ required: true, message: '请输入可供采购类型!' }] }
              ]">
              <a-select-option value="1">食品生鲜</a-select-option>
              <a-select-option value="2">家用电器</a-select-option>
              <a-select-option value="3">办公用品</a-select-option>
              <a-select-option value="4">日常杂货</a-select-option>
            </a-select>
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

const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'supplierEdit',
  props: {
    supplierEditVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.supplierEditVisiable
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
    setFormValues ({...supplier}) {
      this.rowId = supplier.id
      let fields = ['supplierName', 'purchaseType', 'remark']
      let obj = {}
      Object.keys(supplier).forEach((key) => {
        if (key === 'purchaseType') {
          supplier[key] = supplier[key].toString()
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = supplier[key]
        }
      })
      this.form.setFieldsValue(obj)
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
      this.form.validateFields((err, values) => {
        values.id = this.rowId
        if (!err) {
          this.loading = true
          this.$put('/cos/supplier-info', {
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
