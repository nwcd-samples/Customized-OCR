# 解析程序配置文件说明
本文主要列举解析程序配置文件参数，说明相关参数功能和可配置内容。  
配置文件采用YAML格式。
## 1 快速上手
获取身份证的模板示例
```YAML
TemplateName: 'identity-card'
Targets:
  - Name: "姓名"
  - Name: "性别"
    LengthMax: 1
  - Name: "民族"
  - Name: "出生日期"
    KeyWordList: ["出生"]
  - Name: "住址"
    LineCountMax: 2
  - Name: "公民身份号码"
```
返回结果示例
```JSON
[{"tableList":[],
  "keyValueList":[
      {"confidence":"0.99314135","name":"姓名","value":"张三"},
      {"confidence":"0.9980473","name":"性别","value":"男"},
      {"confidence":"0.9970898","name":"民族","value":"汉"},
      {"confidence":"0.99692225","name":"出生日期","value":"1989年06月01日"},
      {"confidence":"0.9936431","name":"住址","value":"北京市朝阳区酒仙桥"},
      {"confidence":"0.998985","name":"公民身份号码","value":"532101198906010015"}]}]
```
示例参数说明：
- TemplateName，YAML文件自描述内容
- Targets内容为一个列表，是配置文件主体，所有需要提取内容都需要在这里配置，需要列举出所有Name。
  - Name，返回关键字，同时也会把此Name作为keyword搜索。比如身份证里的“姓名”，这样既可获取到姓名内容“张三”
  - LengthMax，最大匹配长度
  - KeyWordList，搜索关键字可以和Name不一致，也可以列举多个。
  - LineCountMax，最大配置行数
  
## 2 参数列表
数据类型：
- string，普通字符串
- enumeration，枚举，只能填指定内容的string
- float，小数
- int，整数
- list，列表
- bool，布尔值

通用参数

| 参数名 | 层级 | 是否必填 | 类型 | 默认值 | 说明 |
| ----  | ---- | ---- | ---- | ---- | ---- |
|TemplateName|一级|否|string|无|本文件自描述|
|Targets|一级|是|list|无|所有要提取内容|
|Name|二级|是|string|无|返回关键字，同时也会把此Name作为keyword搜索|
|RecognitionType|二级|否|enumeration|default|识别类型，可选值为：default、table<br>分别代表普通的key/value形式、表格形式|

### 2.1 key/value
当RecognitionType的值为default时，可选参数如下：  

| 参数名 | 层级 | 是否必填 | 类型 | 默认值 | 说明 |
| ----  | ---- | ---- | ---- | ---- | ---- |
|KeyWordList|二级|否|list|无|待搜索的关键字，可以让返回的关键字和待搜索的关键字不同|
|LengthMin|二级|否|int|1|value最小匹配长度|
|LengthMax|二级|否|int|20|value最大匹配长度|
|LineCountMax|二级|否|int|1|最大匹配行数|
|TargetValueType|二级|否|enumeration|string|value类型，可选值为：string、currency、number|
|XRangeMin|二级|否|float|0.0|key在页面上纵坐标的最小值，如果页面上有多个key相同，需要用坐标进行区分|
|XRangeMax|二级|否|float|1.0|key在页面上纵坐标的最大值|
|YRangeMin|二级|否|float|0.0|key在页面上横坐标的最小值|
|YRangeMax|二级|否|float|1.0|key在页面上横坐标的最大值|
|TopOffsetRadio|二级|否|float|1.0|valueItem.top >= keyItem.top - keyItem.height * TopOffsetRadio|
|BottomOffsetRadio|二级|否|float|1.0|valueItem.bottom <= keyItem.bottom + keyItem.height * BottomOffsetRadio|
|LeftOffsetRadio|二级|否|float|0.0|valueItem.left >= keyItem.right - keyItem.width * LeftOffsetRadio|
|RightOffsetRadio|二级|否|float|10.0|valueItem.right <= keyItem.right + keyItem.width * RightOffsetRadio|

### 2.2 表格
当RecognitionType的值为table时，可选参数如下：  

| 参数名 | 层级 | 是否必填 | 类型 | 默认值 | 说明 |
| ----  | ---- | ---- | ---- | ---- | ---- |
|MaxRowHeightRatio|二级|否|float|2.0|用来迭代查找的行高，以headColumn * MaxRowHeightRatio|
|MaxRowCount|二级|否|int|20|最大行数，超过的丢弃|
|Columns|二级|是|list|无|定义所有列|
|ColumnName|三级|是|string|无|列名|
|Location|三级|否|bool|false|用于表格定位，选择2列|
|MainColumn|三级|否|bool|false|按此列来划分行，如果都不设置，默认为第1列。只能设置1列|
|KeyWordList|三级|否|list|无|列头可能出现的关键字|
|MarginLeftType|三级|否|enumeration|middle|可选值为near、middle、far<br>near：以当前Column的左边作为分界线，范围最近<br>middle：以当前Column和左边Column的中点作为分界线，范围适中<br>far：以左边Column的右边作为分界线，范围最远|
|MoveLeftRatio|三级|否|float|0.0|左分界线在MarginLeftType基础上移动的比率，向左为负，向右为正。<br>移动距离为headColumn.width * MoveLeftRatio|
|MarginRightType|三级|否|enumeration|middle|可选值为near、middle、far<br>near：以当前Column的右边作为分界线，范围最近<br>middle：以当前Column和右边Column的中点作为分界线，范围适中<br>far：以右边Column的左边作为分界线，范围最远|
|MoveRightRatio|三级|否|float|0.0|右分界线在MarginRightType基础上移动的比率，向左为负，向右为正。<br>移动距离为headColumn.width * MoveRightRatio|
|XRangeMin|三级|否|float|0.0|key在页面上纵坐标的最小值，如果页面上有多个key相同，需要用坐标进行区分|
|XRangeMax|三级|否|float|1.0|key在页面上纵坐标的最大值|
|YRangeMin|三级|否|float|0.0|key在页面上横坐标的最小值|
|YRangeMax|三级|否|float|1.0|key在页面上横坐标的最大值|

## 3 示例
```YAML
TemplateName: 'table'
Targets:
  - Name: "公司名称"
  - Name: "纳税人识别号"
  - Name: "打印人"
    KeyWordList: ["打印人：", "打印人:"]
  - Name: "分店"
  - Name: "打印时间"
  - Name: "结算单号"
  - Name: "结算年月"
  - Name: "供应商名称"
    MaxLength: 40
  - Name: "结算费用单明细"
    RecognitionType: "table"
    Columns:
      - ColumnName: "扣款代码"
        Location: true
        MoveRightRatio: 0.2

      - ColumnName: "费用项目"
        MoveLeftRatio: 0.3
        MoveRightRatio: 0.3

      - ColumnName: "扣款项目名称"

      - ColumnName: "扣款金额"
        Location: true
        KeyWordList: ["扣款额"]
```