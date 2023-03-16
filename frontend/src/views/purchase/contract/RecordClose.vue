<template>
  <a-modal v-model="show" title="物品退回" @cancel="onClose" :width="900">
    <template slot="footer">
      <a-button key="back1" @click="goodBack" v-if="goodsStatus">
        退货
      </a-button>
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="recordData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">基础信息</span></a-col>
        <a-col :span="8"><b>入库单号：</b>
          {{ recordData.code !== null ? recordData.code : '- -' }}
        </a-col>
        <a-col :span="8"><b>价 格：</b>
          {{ recordData.totalPrice }} 元
        </a-col>
        <a-col :span="8"><b>保管人：</b>
          {{ recordData.custodianName }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>入库时间：</b>
          {{ recordData.createDate !== null ? recordData.createDate : '- -' }}
        </a-col>
        <a-col :span="8"><b>经手人：</b>
          {{ recordData.handlerName }}
        </a-col>
        <a-col :span="8"><b>备 注：</b>
          {{ recordData.remark }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>供应商：</b>
          {{ recordData.supplierName !== null ? recordData.supplierName : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;" :gutter="15">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">物品详情</span></a-col>
        <a-col :span="24">
          <a-table :columns="columns" :data-source="goodsList">
            <template slot="nameShow" slot-scope="text, record">
              <a-tooltip>
                <template slot="title">
                  {{ record.materialName }}
                </template>
                {{ record.materialName.slice(0, 18) }} ...
              </a-tooltip>
            </template>
            <template slot="modelShow" slot-scope="text, record">
              <a-tooltip>
                <template slot="title">
                  {{ record.model }}
                </template>
                {{ record.model.slice(0, 10) }} ...
              </a-tooltip>
            </template>
            <template slot="typeIdShow" slot-scope="text, record">
              <span v-if="record.storeMax >= record.quantity">{{ record.storeMax }}</span>
              <span v-else style="color: red">{{ record.storeMax }}</span>
            </template>
          </a-table>
        </a-col>
      </a-row>
    </div>
  </a-modal>
</template>

<script>
import moment from 'moment'
moment.locale('zh-cn')
export default {
  name: 'RecordView',
  props: {
    recordShow: {
      type: Boolean,
      default: false
    },
    recordData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.recordShow
      },
      set: function () {
      }
    },
    columns () {
      return [{
        title: '物品名称',
        dataIndex: 'materialName',
        scopedSlots: {customRender: 'nameShow'}
      }, {
        title: '型号',
        dataIndex: 'model',
        scopedSlots: {customRender: 'modelShow'}
      }, {
        title: '数量',
        dataIndex: 'quantity'
      }, {
        title: '当前库存',
        dataIndex: 'storeMax',
        scopedSlots: {customRender: 'typeIdShow'}
      }, {
        title: '单位',
        dataIndex: 'measurementUnit'
      }, {
        title: '单价',
        dataIndex: 'unitPrice',
        customRender: (text, row, index) => {
          if (text !== null) {
            return '￥' + text.toFixed(2)
          } else {
            return '- -'
          }
        }
      }]
    }
  },
  data () {
    return {
      loading: false,
      goodsList: [],
      goodsStatus: false
    }
  },
  watch: {
    recordShow: function (value) {
      if (value) {
        this.getGoodsByNum(this.recordData.code)
      }
    }
  },
  methods: {
    getGoodsByNum (num) {
      this.$get(`/cos/storage-record/returned/purchase/${num}`).then((r) => {
        this.goodsList = r.data.gooods
        this.goodsStatus = r.data.status
        console.log(r.data)
      })
    },
    goodBack () {
      this.$get(`/cos/storage-record/returned/${this.recordData.code}`).then((r) => {
        this.$emit('success')
      })
    },
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
  /deep/ .ant-table-tbody {
    font-size: 12px;
  }
  /deep/ .ant-table-thead {
    font-size: 13px;
  }
</style>
