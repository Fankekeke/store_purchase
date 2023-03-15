<template>
  <a-drawer
    title="出库"
    :maskClosable="false"
    placement="right"
    :closable="false"
    :visible="show"
    :width="1200"
    @close="onClose"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;"
  >
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="12">
          <a-form-item label='保管人' v-bind="formItemLayout">
            <a-select style="width: 100%" v-model="custodian" option-label-prop="label">
              <a-select-option v-for="(item, index) in staffList" :key="index" :value="item.staffCode" :label="item.staffName">
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
        <a-col :span="12">
          <a-form-item label='经手人' v-bind="formItemLayout">
            <a-select style="width: 100%" v-model="handler" option-label-prop="label">
              <a-select-option v-for="(item, index) in staffList" :key="index" :value="item.staffCode" :label="item.staffName">
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
        <a-col :span="24">
          <a-form-item label='备注消息' v-bind="formItemLayout">
            <a-textarea :rows="4" v-decorator="[
            'remark',
             { rules: [{ required: true, message: '请输入名称!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-table :columns="columns" :data-source="dataList">
            <template slot="nameShow" slot-scope="text, record">
              <a-input v-model="record.materialName"></a-input>
            </template>
            <template slot="typeShow" slot-scope="text, record">
              <a-input v-model="record.model"></a-input>
            </template>
            <template slot="typeIdShow" slot-scope="text, record">
              <a-select v-model="record.materialType" allowClear>
                <a-select-option :value="item.id" v-for="(item, index) in productTypeList" :key="index">{{ item.name }}</a-select-option>
              </a-select>
            </template>
            <template slot="unitShow" slot-scope="text, record">
              <a-input v-model="record.measurementUnit"></a-input>
            </template>
            <template slot="amountShow" slot-scope="text, record">
              <a-input-number v-model="record.quantity" :min="1" :max="record.max !== undefined ? record.max : 999999" :step="1"/>
            </template>
            <template slot="priceShow" slot-scope="text, record">
              <a-input-number v-model="record.unitPrice" :min="1"/>
            </template>
          </a-table>
<!--          <a-button @click="dataAdd" type="primary" ghost size="large" style="margin-top: 10px;width: 100%">-->
<!--            新增物品-->
<!--          </a-button>-->
        </a-col>
      </a-row>
    </a-form>
    <div class="drawer-bootom-button">
      <a-popconfirm title="确定放弃编辑？" @confirm="onClose" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading">提交</a-button>
    </div>
  </a-drawer>
</template>

<script>
import {mapState} from 'vuex'
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'StockOut',
  props: {
    stockoutVisiable: {
      default: false
    },
    stockoutData: {
      type: Array
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.stockoutVisiable
      },
      set: function () {
      }
    },
    columns () {
      return [{
        title: '物品名称',
        dataIndex: 'name',
        scopedSlots: {customRender: 'nameShow'}
      }, {
        title: '型号',
        dataIndex: 'type',
        scopedSlots: {customRender: 'typeShow'}
      }, {
        title: '数量',
        dataIndex: 'amount',
        scopedSlots: {customRender: 'amountShow'}
      }, {
        title: '所属类型',
        dataIndex: 'typeId',
        width: 200,
        scopedSlots: {customRender: 'typeIdShow'}
      }, {
        title: '单位',
        dataIndex: 'unit',
        scopedSlots: {customRender: 'unitShow'}
      }, {
        title: '单价',
        dataIndex: 'price',
        scopedSlots: {customRender: 'priceShow'}
      }]
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      dataList: [],
      staffList: [],
      handler: null,
      custodian: null,
      materialList: [],
      productTypeList: []
    }
  },
  watch: {
    stockoutVisiable: function (value) {
      if (value) {
        this.dataList = this.stockoutData
      }
    }
  },
  mounted () {
    this.getStaffList()
    this.selectProductType()
  },
  methods: {
    selectProductType () {
      this.$get(`/cos/product-type-info/list`).then((r) => {
        this.productTypeList = r.data.data
      })
    },
    getStaffList () {
      this.$get('/cos/staff-info/list').then((r) => {
        this.staffList = r.data.data
      })
    },
    reset () {
      this.loading = false
      this.dataList = []
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      if (this.handler == null || this.custodian == null || this.dataList.length === 0) {
        this.$message.error('请添加出库记录！')
      }
      this.form.validateFields((err, values) => {
        if (!err) {
          values.material = JSON.stringify(this.dataList)
          values.handlerCode = this.handler
          values.custodianCode = this.custodian
          this.loading = true
          this.$post('/cos/out-stock-record', {
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
