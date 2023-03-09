<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="物料名称"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 18, offset: 2}">
                <a-input v-model="queryParams.materialName"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="型号"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 18, offset: 2}">
                <a-input v-model="queryParams.model"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="物料类型"
                :labelCol="{span: 4}"
                :wrapperCol="{span: 18, offset: 2}">
                <a-select v-model="queryParams.materialType" allowClear>
                  <a-select-option value="1">食品生鲜</a-select-option>
                  <a-select-option value="2">家用电器</a-select-option>
                  <a-select-option value="3">办公用品</a-select-option>
                  <a-select-option value="4">日常杂货</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="add">出库</a-button>
        <a-button @click="replenishment">盘库</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="titleShow" slot-scope="text, record">
          <template>
            <a-badge status="processing"/>
            <a-tooltip>
              <template slot="title">
                {{ record.title }}
              </template>
              {{ record.title.slice(0, 8) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="contentShow" slot-scope="text, record">
          <template>
            <a-tooltip>s
              <template slot="title">
                {{ record.content }}
              </template>
              {{ record.content.slice(0, 30) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon type="bulb" theme="twoTone" twoToneColor="#4a9ff5" @click="view(record)" title="详 情" style="margin-right: 15px"></a-icon>
        </template>
      </a-table>
    </div>
    <storehouse-view
      @close="handlestorehouseViewClose"
      :storehouseShow="storehouseView.visiable"
      :storehouseData="storehouseView.data">
    </storehouse-view>
    <stock-out
      @close="handleStockoutClose"
      @success="handleStockoutSuccess"
      :stockoutData="stockout.data"
      :stockoutVisiable="stockout.visiable">
    </stock-out>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
import StockOut from './StockOut'
import storehouseView from './StorehouseView'
moment.locale('zh-cn')

export default {
  name: 'storehouse',
  components: {storehouseView, StockOut, RangeDate},
  data () {
    return {
      advanced: false,
      storehouseAdd: {
        visiable: false
      },
      storehouseEdit: {
        visiable: false
      },
      storehouseView: {
        visiable: false,
        data: null
      },
      stockout: {
        visiable: false,
        data: null
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      }
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '物料名称',
        dataIndex: 'materialName'
      }, {
        title: '物料类型',
        dataIndex: 'materialType',
        customRender: (text, row, index) => {
          switch (text) {
            case 1:
              return <a-tag>食品生鲜</a-tag>
            case 2:
              return <a-tag>家用电器</a-tag>
            case 3:
              return <a-tag>办公用品</a-tag>
            case 4:
              return <a-tag>日常杂货</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '型号',
        dataIndex: 'model'
      }, {
        title: '当前库存',
        dataIndex: 'quantity',
        customRender: (text, row, index) => {
          return text + row.measurementUnit
        }
      }, {
        title: '单价',
        dataIndex: 'unitPrice',
        customRender: (text, row, index) => {
          return text + '元'
        }
      }, {
        title: '操作时间',
        dataIndex: 'createDate',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    view (record) {
      this.storehouseView.visiable = true
      this.storehouseView.data = record
    },
    onSelectChange (selectedRowKeys, selectedRows) {
      selectedRows.forEach(item => {
        if (item.amount === 0) {
          this.$message.warning('该物品没有库存！')
          return false
        }
      })
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },
    replenishment () {
      this.$get('/cos/storehouse-info/replenishment').then((r) => {
        this.$message.success('等在盘库~请稍等')
      })
    },
    add () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要出库的物品')
        return
      }
      let goods = this.selectedRows
      goods.forEach(item => {
        item.max = item.quantity
        item.materialType = item.materialType.toString()
      })
      this.stockout.data = JSON.parse(JSON.stringify(goods))
      this.stockout.visiable = true
    },
    handleStockoutClose () {
      this.stockout.visiable = false
    },
    handleStockoutSuccess () {
      this.stockout.visiable = false
      this.selectedRows = []
      this.selectedRowKeys = []
      this.$message.success('出库成功')
      this.search()
    },
    handlestorehouseAddClose () {
      this.storehouseAdd.visiable = false
    },
    handlestorehouseAddSuccess () {
      this.storehouseAdd.visiable = false
      this.$message.success('更新成功')
      this.search()
    },
    edit (record) {
      this.$refs.storehouseEdit.setFormValues(record)
      this.storehouseEdit.visiable = true
    },
    handlestorehouseEditClose () {
      this.storehouseEdit.visiable = false
    },
    handlestorehouseEditSuccess () {
      this.storehouseEdit.visiable = false
      this.$message.success('修改成功')
      this.search()
    },
    handlestorehouseViewClose () {
      this.storehouseView.visiable = false
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/storehouse-info/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      if (params.materialType === undefined) {
        delete params.materialType
      }
      this.$get('/cos/storehouse-info/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
