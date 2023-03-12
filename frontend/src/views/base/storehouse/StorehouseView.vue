<template>
  <a-modal v-model="show" title="物品出入库记录" @cancel="onClose" :width="900">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="storehouseData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">基础信息</span></a-col>
        <a-col :span="8"><b>物料名称：</b>
          {{ storehouseData.materialName !== null ? storehouseData.materialName : '- -' }}
        </a-col>
        <a-col :span="8"><b>物料类型：</b>
          <span>{{ storehouseData.materialTypeName }}</span>
        </a-col>
        <a-col :span="8"><b>物料类型：</b>
          {{ storehouseData.model !== null ? storehouseData.model : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>当前库存：</b>
          {{ storehouseData.quantity !== null ? storehouseData.quantity : '- -' }}
        </a-col>
        <a-col :span="8"><b>计量单位：</b>
          {{ storehouseData.measurementUnit !== null ? storehouseData.measurementUnit : '- -' }}
        </a-col>
        <a-col :span="8"><b>单价（元）：</b>
          {{ storehouseData.unitPrice !== null ? storehouseData.unitPrice : '- -' }}
        </a-col>
      </a-row>
      <br/>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">出入库记录</span></a-col>
        <a-col :span="24">
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" tab="出库">
              <a-table :columns="columns" :data-source="outData">
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
                  <span>{{ record.materialTypeName }}</span>
                </template>
              </a-table>
            </a-tab-pane>
            <a-tab-pane key="2" tab="入库">
              <a-table :columns="columns" :data-source="inData">
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
                  <span>{{ record.materialTypeName }}</span>
                </template>
              </a-table>
            </a-tab-pane>
          </a-tabs>
        </a-col>
      </a-row>
      <br/>
    </div>
  </a-modal>
</template>

<script>
export default {
  name: 'storehouseView',
  props: {
    storehouseShow: {
      type: Boolean,
      default: false
    },
    storehouseData: {
      type: Object
    }
  },
  computed: {
    show: {
      get: function () {
        return this.storehouseShow
      },
      set: function () {
      }
    },
    columns () {
      return [{
        title: '单号',
        dataIndex: 'orderNumber'
      }, {
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
        title: '所属类型',
        dataIndex: 'materialType',
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
      inData: [],
      outData: []
    }
  },
  watch: {
    storehouseShow: function (value) {
      if (value) {
        this.getOutInDetail(this.storehouseData.materialName)
      }
    }
  },
  methods: {
    getOutInDetail (name) {
      this.$get(`/cos/storehouse-info/${name}`).then((r) => {
        this.inData = r.data.in
        this.outData = r.data.out
      })
    },
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
</style>
