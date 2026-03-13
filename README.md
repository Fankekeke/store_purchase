### 基于SpringBoot + Vue的超市采购管理系统.

超市进货管理系统、零售采购平台、商品采购管理系统、供应链采购系统、超市库存与采购系统

#### 管理员功能模块介绍：
> 供应商准入：审核并管理供应商资质，控制合作准入权限。订单管理：创建、跟踪和审核采购订单全流程状态。入库记录：查看所有商品入库的时间、数量及操作人员信息。员工管理：维护采购、库管等岗位员工账号与权限分配。库房信息：管理各库房的基本资料、位置及存储容量配置。库房盘库：组织并记录定期库存盘点，确保账实相符。产品类别：维护商品分类体系，便于采购与库存归类管理。

#### 采购员功能模块介绍：
> 订单管理：发起采购申请，跟进订单执行与到货情况。退货管理：处理不合格或多余商品的退货流程及记录。采购合同管理：拟定、上传和管理与供应商签订的合同文件。库房信息：查看库房位置、容量及当前库存概况，辅助采购决策。

#### 库房管理员功能模块介绍：
> 库房信息：实时掌握所负责库房的基本参数与使用状态。出库记录：登记商品出库时间、去向、数量及领用人信息。入库记录：核对并录入采购到货商品的入库明细数据。盘库统计：执行盘点任务，生成差异报告与库存汇总报表。

#### 安装环境

JAVA 环境 

Node.js环境 [https://nodejs.org/en/] 选择14.17

Yarn 打开cmd， 输入npm install -g yarn !!!必须安装完毕nodejs

Mysql 数据库 [https://blog.csdn.net/qq_40303031/article/details/88935262] 一定要把账户和密码记住

redis

Idea 编译器 [https://blog.csdn.net/weixin_44505194/article/details/104452880]

WebStorm OR VScode 编译器 [https://www.jianshu.com/p/d63b5bae9dff]

#### 采用技术及功能

后端：SpringBoot、MybatisPlus、MySQL、Redis、
前端：Vue、Apex、Antd、Axios、baidu.js
报表：SpreadJS

平台后台：springboot(框架) + redis(缓存中间件) + shiro(权限中间件) + mybatisplus(orm) + restful风格接口 + mysql(数据库)

开发环境：windows10 or windows7 ， vscode or webstorm ， idea + lambok

#### 前台启动方式

安装所需文件 yarn install 
运行 yarn run dev

#### 后端启动方式

1.首先启动redis，进入redis目录终端。输入redis-server回车
2.导入sql文件，修改数据库与redis连接配置
3.idea中启动后端项目

### 管理员
供应商准入，订单管理，入库记录，员工管理，库房信息，库房盘库，产品类别

### 采购员
订单管理，退货管理，采购合同管理，库房信息

### 库房管理员
库房信息，出库记录，入库记录，盘库统计

#### 默认后台账户密码

[管理员]
system
1234qwer

[采购员]
caigou
1234qwer

[库房管理员]
sys_store
1234qwer

#### 项目截图

|  |  |
|---------------------|---------------------|
|![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868449983.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868654519.png) |
|![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868423491.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868633350.png) |
|![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868394681.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868578419.png) |
|![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868375427.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868568011.png) |
|![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868348161.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868554338.png) |
|![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868693704.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868535310.png) |
|![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868245655.jpg) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1683868498352.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/work/936e9baf53eb9a217af4f89c616dc19.png) |

#### 演示视频

暂无

#### 获取方式

Email: fan1ke2ke@gmail.com

WeChat: `Storm_Berserker`

`附带部署与讲解服务，因为要恰饭资源非免费，伸手党勿扰，谢谢理解😭`

> 1.项目纯原创，不做二手贩子 2.一次购买终身有效 3.项目讲解持续到答辩结束 4.非常负责的答辩指导 5.**黑奴价格**

> 项目部署调试不好包退！功能逻辑没讲明白包退！

#### 其它资源

[2025年-答辩顺利通过-客户评价🍜](https://berserker287.github.io/2025/06/18/2025%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2024年-答辩顺利通过-客户评价👻](https://berserker287.github.io/2024/06/06/2024%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2023年-答辩顺利通过-客户评价🐢](https://berserker287.github.io/2023/06/14/2023%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2022年-答辩通过率100%-客户评价🐣](https://berserker287.github.io/2022/05/25/%E9%A1%B9%E7%9B%AE%E4%BA%A4%E6%98%93%E8%AE%B0%E5%BD%95/)

[毕业答辩导师提问的高频问题](https://berserker287.github.io/2023/06/13/%E6%AF%95%E4%B8%9A%E7%AD%94%E8%BE%A9%E5%AF%BC%E5%B8%88%E6%8F%90%E9%97%AE%E7%9A%84%E9%AB%98%E9%A2%91%E9%97%AE%E9%A2%98/)

[50个高频答辩问题-技术篇](https://berserker287.github.io/2023/06/13/50%E4%B8%AA%E9%AB%98%E9%A2%91%E7%AD%94%E8%BE%A9%E9%97%AE%E9%A2%98-%E6%8A%80%E6%9C%AF%E7%AF%87/)

[计算机毕设答辩时都会问到哪些问题？](https://www.zhihu.com/question/31020988)

[计算机专业毕业答辩小tips](https://zhuanlan.zhihu.com/p/145911029)


#### 接JAVAWEB毕设，纯原创，价格公道，诚信第一

`网站建设、小程序、H5、APP、各种系统 选题+开题报告+任务书+程序定制+安装调试+项目讲解+论文+答辩PPT`

More info: [悲伤的橘子树](https://berserker287.github.io/)

<p><img align="center" src="https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/%E5%90%88%E4%BD%9C%E7%89%A9%E6%96%99%E6%A0%B7%E5%BC%8F%20(3).png" alt="fankekeke" /></p>
