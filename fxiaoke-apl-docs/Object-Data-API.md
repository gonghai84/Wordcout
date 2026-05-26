# 纷享销客 APL Object Data API 技术文档

> **文档版本**: 2026-04-10  
> **适用范围**: Fx.object 主入口文档

---

## 目录

1. [数据创建](#1-数据创建)
2. [数据更新](#2-数据更新)
3. [数据删除](#3-数据删除)
4. [数据查询](#4-数据查询)
5. [数据校验](#5-数据校验)
6. [团队成员管理](#6-团队成员管理)
7. [数据锁定](#7-数据锁定)

---

## 1. 数据创建

### 1.1 create - 主从对象同时入库

**签名**:
```groovy
Fx.object.create(String apiName, Map objectData, Map details, CreateAttribute createAttribute)
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| apiName | String | 是 | 主对象 API Name |
| objectData | Map | 是 | 主对象字段值 |
| details | Map | 是 | 从对象数据，键为从对象 API Name |
| createAttribute | CreateAttribute | 是 | 创建控制参数 |

**返回结构**:
- `isError/error`: 是否异常
- `data`: 新建结果
- `message`: 提示信息

**注意事项**:
- details 传空表示不创建从对象
- 查重阻断也可能进入错误分支，需从 data 中取重复信息

**最小示例**:
```groovy
Map masterData = [
  "name": "主从同时新建1",
  "owner": ["1000"]
]
Map detailData = [
  "object_detail1__c": [
    ["name": "张三1"]
  ]
]

def (Boolean error, Map data, String errorMessage) = Fx.object.create(
  "object_1yO4J__c",
  masterData,
  detailData,
  CreateAttribute.builder().build()
)
```

---

### 1.2 batchCreate - 批量创建数据

**签名**:
```groovy
Fx.object.batchCreate(String apiName, List objects, CreateAttribute attribute)
```

**参数说明**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| apiName | String | 是 | 对象 API Name |
| objects | List[Map] | 是 | 每一项是一条对象数据 |
| attribute | CreateAttribute | 是 | 创建控制参数 |

**返回结构**: `List[Map]`

**限制**: 单批最多 500 行

**最小示例**:
```groovy
List objects = [
  ["name": "客户A", "owner": ["1000"]],
  ["name": "客户B", "owner": ["1000"]]
]

def (Boolean error, List<Map> data, String errorMessage) = Fx.object.batchCreate(
  "AccountObj",
  objects,
  CreateAttribute.builder().build()
)
```

---

### 1.3 copyByRule - 按映射规则复制创建

**签名**:
```groovy
Fx.object.copyByRule(String sourceApiName, String sourceId, String ruleApiName, Map masterPlus, Map detailPlus)
```

**适用场景**:
- 根据映射规则把 A 对象复制为 B 对象
- 在映射基础上补充主对象或从对象字段值

**注意事项**:
- 该方法创建的数据可以触发审批流和工作流

**最小示例**:
```groovy
Map masterPlus = ["field_wbYI0__c": "函数补充值"]
Map detailPlus = [
  "object_snpWU__c": [
    ["field_a72ov__c": "明细补充值"]
  ]
]

def (Boolean error, Object result, String errorMessage) = Fx.object.copyByRule(
  "object_pbx98__c",
  "66d827e45c1ac90001ede05c",
  "map_y5iy4__c",
  masterPlus,
  detailPlus
)
```

---

## 2. 数据更新

### 2.1 update.increment - 增量更新

**签名**:
```groovy
Fx.object.update(String apiName, String objectId, Map updateFields, UpdateAttribute attribute)
```

**特点**:
- 不校验锁定状态
- 不触发编辑按钮前验证、后动作以及验证规则
- 更适合字段级修改

**注意事项**:
- 不支持直接更新负责人、公式字段、统计字段等
- 币种、汇率类字段不能被更新为空

**最小示例**:
```groovy
Map updateFields = ["name": "新的名称"]

def (Boolean error, Map data, String errorMessage) = Fx.object.update(
  "object_s82CA__c",
  "64b1113e87ec1c0001bfc102",
  updateFields,
  UpdateAttribute.builder().triggerWorkflow(true).build()
)
```

---

### 2.2 update.edit - 主从覆盖更新

**签名**:
```groovy
Fx.object.update(String apiName, String objectId, Map updateFields, Map detailData, ActionAttribute actionAttribute)
```

**特点**:
- 适合主从整体重写
- detailData 传空集合会清空从对象
- detailData 传 null 表示不更新从对象

**注意事项**:
- 无法更新锁定数据
- 如需锁定数据更新，优先评估 update.increment

**最小示例**:
```groovy
Map updateFields = ["name": "主对象名称"]
Map detailData = [
  "object_detail__c": [
    ["name": "明细1"],
    ["name": "明细2"]
  ]
]

def (Boolean error, Map data, String errorMessage) = Fx.object.update(
  "object_qs2nb__c",
  "607d5e3dd02b9f00016507d8",
  updateFields,
  detailData,
  ActionAttribute.create()
)
```

---

### 2.3 update.byQuery - 按条件批量更新

**签名**:
```groovy
Fx.object.update(String apiName, QueryTemplate template, Map updateFields, UpdateAttribute attribute)
```

**特点**:
- 默认最多更新 1000 条
- 若需更新超过 1000 条，需设置 `isAllUpdate=true`

**注意事项**:
- 原文标注为灰度能力
- 大批量更新耗时较高，1000 条约 3 分钟
- 若设置 `runBusiness=false` 或类似绕过业务逻辑参数，应谨慎评估

**最小示例**:
```groovy
QueryTemplate query = QueryTemplate.AND([
  "name": QueryOperator.EQ("主从同时新建1")
])

def (Boolean error, Object result, String errorMessage) = Fx.object.update(
  "object_1yO4J__c",
  query,
  ["field__c": "test"],
  UpdateAttribute.builder().build()
)
```

---

### 2.4 batchUpdate - 批量更新指定字段

**签名**:
```groovy
Fx.object.batchUpdate(String apiName, Map objects, List fields, BatchUpdateAttribute attribute)
```

**参数说明**:
- `objects` 的 key 为数据 ID，value 为待更新字段 Map
- `fields` 决定本次实际写入的字段范围

**高风险说明**:
- 这是偏底层接口，接近直接更新数据库
- fields 中出现但 objects 未提供值时，表示清空字段
- 不建议用于计算、统计、引用字段
- 自定义对象单批最多 500 条

**推荐**:
- 预制对象优先考虑 `convert2SingleOperation=true`
- 数据量少时，直接循环调用 `update.increment` 更可控

**最小示例**:
```groovy
Map objects = [
  "60acc4a2d040a70001886739": ["field_bVch6__c": "test1"],
  "60acc482d040a70001886582": ["field_bVch6__c": "test2"]
]
List fields = ["field_bVch6__c"]

def (Boolean error, List result, String errorMessage) = Fx.object.batchUpdate(
  "object_8N0H2__c",
  objects,
  fields,
  BatchUpdateAttribute.builder().build()
)
```

---

### 2.5 editTeamMember - 编辑内部团队成员

**签名**:
```groovy
Fx.object.editTeamMember(String apiName, String dataId, List teamMembers, Boolean ignoreSendingRemind)
```

**团队成员结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | String | 团队成员 ID |
| permission | Integer | 1-只读，2-读写 |
| role | Integer | 相关团队角色 |
| type | Integer | 成员类型 |

**注意事项**:
- 只能修改内部相关团队
- 实际取值以系统定义为准

**最小示例**:
```groovy
List teamMembers = [
  ["userId": "1058", "role": 4, "permission": 1],
  ["userId": "1057", "role": 4, "permission": 2]
]

def result = Fx.object.editTeamMember(
  "AccountObj",
  "36fd270a986842529445bf3d252cca9b",
  teamMembers,
  false
).result() as Map
```

---

## 3. 数据删除

### 3.1 directDelete - 直接删库

**签名**:
```groovy
Fx.object.directDelete(String apiName, String dataId)
```

**警告**: 这是全篇最危险接口之一，只适合确认无误的底层清理场景。数据不可恢复。

---

### 3.2 batchDelete - 批量直接删除

**签名**:
```groovy
Fx.object.batchDelete(String apiName, List objectIds)
```

**注意事项**:
- 不区分对象是否作废
- 会直接彻底删除

---

### 3.3 deleteTeamMember - 删除团队成员

**签名**:
```groovy
Fx.object.deleteTeamMember(String apiName, List objectIds, List teamMembers, List outTeamMemberEmployee, Boolean ignoreSendingRemind)
```

**外部团队成员结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | String | 下游企业人员 ID |
| outTenantId | String | 下游企业 ID |

---

### 3.4 bulkDelete - 批量彻底删除已作废数据

**签名**:
```groovy
Fx.object.bulkDelete(String apiName, List objectIds)
```

**注意事项**:
- 仅用于回收站中已作废数据
- 原文建议单次不要超过 20 条

---

### 3.5 delete - 彻底删除单条已作废数据

**签名**:
```groovy
Fx.object.delete(String apiName, String objectId)
```

---

## 4. 数据查询

### 4.1 find - FQL 查询多条数据

**签名**:
```groovy
Fx.object.find(String apiName, FQLAttribute fqlAttribute, SelectAttribute selectAttribute)
```

**返回体重点**:

| 字段 | 类型 | 说明 |
|------|------|------|
| size | Integer | 本次返回条数 |
| total | Integer | 总条数 |
| data | List | 数据列表 |

**最小示例**:
```groovy
FQLAttribute fql = FQLAttribute.builder()
  .columns(["_id", "name"])
  .queryTemplate(QueryTemplate.AND(["name": QueryOperator.EQ("account1")]))
  .build()

SelectAttribute selectAttribute = SelectAttribute.builder().needInvalid(false).build()

def (Boolean error, QueryResult queryResult, String errorMessage) = Fx.object.find(
  "AccountObj",
  fql,
  selectAttribute
)
```

---

### 4.2 findOne - FQL 查询单条数据

**签名**:
```groovy
Fx.object.findOne(String apiName, FQLAttribute fqlAttribute, SelectAttribute selectAttribute)
```

**建议**: 查单条优先使用该接口，而不是 find + limit 1

**最小示例**:
```groovy
FQLAttribute fql = FQLAttribute.builder()
  .columns(["_id", "name"])
  .queryTemplate(QueryTemplate.AND(["_id": QueryOperator.EQ("6177cde7a0cb410001930ad0")]))
  .build()

def (Boolean error, Map data, String errorMessage) = Fx.object.findOne(
  "AccountObj",
  fql,
  SelectAttribute.builder().build()
)
```

---

### 4.3 findById - 按 ID 查询单条

**签名**:
```groovy
Fx.object.findById(String apiName, String id, FQLAttribute fqlAttribute, SelectAttribute selectAttribute)
```

---

### 4.4 findByIds - 按 ID 集合查询

**签名**:
```groovy
Fx.object.findByIds(String apiName, List ids, FQLAttribute fqlAttribute, SelectAttribute selectAttribute)
```

---

### 4.5 select.query - SQL 查询

**签名**:
```groovy
Fx.object.select(String sql, SelectAttribute selectAttribute)
```

**注意事项**:
- selectAttribute 为可选参数
- 返回结果可能是 QueryResult，也可能是聚合 List

**最小示例**:
```groovy
String sql = "select _id, name from AccountObj where create_time > 0 limit 10 offset 0"
SelectAttribute attribute = SelectAttribute.builder().needInvalid(false).build()

def result = Fx.object.select(sql, attribute).result() as QueryResult
```

---

### 4.6 select.stream - 大数据量 SQL 流式分页查询

**签名**:
```groovy
Fx.object.select(String sql, SelectAttribute selectAttribute, Closure consumer)
```

**适用场景**:
- 查全量数据
- 需要边查边处理

**限制**:
- 不支持 order by
- 不支持 limit

**最小示例**:
```groovy
String sql = "select _id, name from object_227xW__c where field_rzv5M__c > 100"

Fx.object.select(sql, SelectAttribute.builder().build(), { list ->
  list.each { row ->
    log.info((row as Map)["name"])
  }
}).result()
```

---

### 4.7 findWithRelated - 关联联查

**签名**:
```groovy
Fx.object.findWithRelated(String apiName, String relatedField, List criteria, Map orderBy, Integer limit, Integer skip, ActionAttribute attribute)
```

**说明**:
- 查找关联场景：apiName 传相关对象 API Name，relatedField 传查找关联字段
- 主从场景：apiName 传从对象 API Name，relatedField 传主从关系字段

**最小示例**:
```groovy
def attribute = ActionAttribute.build {
  forceQueryFromDB = false
}

def (Boolean error, QueryResult result, String errorMessage) = Fx.object.findWithRelated(
  "object_0uyAd__c",
  "field_YjJ6d__c",
  [["_id": "60868215965b1900014c0d35"]],
  ["create_time": 1],
  10,
  0,
  attribute
)
```

---

### 4.8 getTeamMember - 获取团队成员

**签名**:
```groovy
Fx.object.getTeamMember(String apiName, String dataId)
```

**返回重点字段**:
- teamMemberEmployee
- teamMemberRole
- teamMemberPermissionType
- teamMemberType
- outTenantId
- teamMemberName

**最小示例**:
```groovy
def rst = Fx.object.getTeamMember("AccountObj", "83cf73d957924284a96e9c44ebb333ec").result() as List
```

---

## 5. 数据校验

### 5.1 duplicateSearch - 获取查重结果

**签名**:
```groovy
Fx.object.duplicateSearch(String apiName, String type, Map data, String relatedApiName, Integer pageNumber, Integer pageSize)
```

**常见 type**:
- `NEW`
- `TOOL`

**结果重点**:
- dataList
- matchType
- keepSave

**最小示例**:
```groovy
Map data = [
  "object_describe_api_name": "object_zPSCw__c",
  "field_619D3__c": "123"
]

def (Boolean error, Map result, String errorMessage) = Fx.object.duplicateSearch(
  "object_zPSCw__c",
  "NEW",
  data,
  null,
  1,
  20
)
```

---

## 6. 团队成员管理

### 6.1 replaceOutTeamMember - 全量替换外部成员

**签名**:
```groovy
Fx.object.replaceOutTeamMember(String apiName, String objectId, Object outTeamMembers, Boolean ignoreSendingRemind)
```

**注意事项**:
- 不可替换外部负责人

**最小示例**:
```groovy
def member = TeamMemberEmployee.builder()
  .userId("309175511")
  .outTenantId("301185430")
  .build()

def outMembers = OutTeamMemberAttribute.createEmployMember(
  [member],
  TeamMemberEnum.Permission.READANDWRITE
)

def result = Fx.object.replaceOutTeamMember(
  "object_qep6N__c",
  "61848edfd9007e00019ee222",
  [outMembers],
  false
)
```

---

### 6.2 addTeamMember - 添加内部团队成员

**签名**:
```groovy
Fx.object.addTeamMember(String apiName, String objectId, Object teamMemberAttribute)
```

**支持添加**:
- 人员
- 用户组
- 部门
- 角色

**注意事项**:
- 不能添加负责人
- 若成员已存在，则会更新原成员信息

**最小示例**:
```groovy
def teamMember = TeamMemberAttribute.createEmployMember(
  ["1027"],
  TeamMemberEnum.Role.NORMAL_STAFF,
  TeamMemberEnum.Permission.READONLY
)

Fx.object.addTeamMember("object_qep6N__c", "61848edfd9007e00019ee222", teamMember).result()
```

---

### 6.3 addOutTeamMember - 添加外部团队成员

**签名**:
```groovy
Fx.object.addOutTeamMember(String apiName, String objectId, Object outTeamMemberAttribute)
```

**支持添加**:
- 外部人员
- 下游企业
- 外部角色
- 下游企业组

**最小示例**:
```groovy
def member = TeamMemberEmployee.builder()
  .userId("309175511")
  .outTenantId("301185430")
  .build()

def teamMember = OutTeamMemberAttribute.createEmployMember(
  [member],
  TeamMemberEnum.Permission.READANDWRITE
)

Fx.object.addOutTeamMember("object_qep6N__c", "61848edfd9007e00019ee222", teamMember).result()
```

---

### 6.4 changeOwner - 单条更换负责人

**签名**:
```groovy
Fx.object.changeOwner(String apiName, String dataId, String ownerId, ActionAttribute attribute)
```

**最小示例**:
```groovy
Fx.object.changeOwner(
  "AccountObj",
  "ed47841898054749a2ec9be9e6e5d728",
  "1001",
  ActionAttribute.create()
).result()
```

---

### 6.5 batchChangeOwner - 批量更换负责人

**签名**:
```groovy
Fx.object.batchChangeOwner(String apiName, List changeData, ActionAttribute attribute)
```

**说明**: changeData 结构为 objectId/ownerId

**最小示例**:
```groovy
List changeData = [
  ["objectId": "5f86b47b1bdac00001f2c300", "ownerId": ["-10000"]],
  ["objectId": "5f86b4a71bdac00001f2d232", "ownerId": ["-10000"]]
]

Fx.object.batchChangeOwner("object_i66LR__c", changeData, ActionAttribute.create()).result()
```

---

## 7. 数据锁定

> 内容待补充

---

## 附录：方法速查表

### 数据创建
| 方法 | 说明 | 限制 |
|------|------|------|
| create | 主从对象同时入库 | - |
| batchCreate | 批量创建 | 单批最多 500 行 |
| copyByRule | 按映射规则复制 | 可触发审批流 |

### 数据更新
| 方法 | 说明 | 限制 |
|------|------|------|
| update.increment | 增量更新 | 不触发验证规则 |
| update.edit | 主从覆盖更新 | 无法更新锁定数据 |
| update.byQuery | 按条件批量更新 | 默认最多 1000 条 |
| batchUpdate | 批量更新指定字段 | 偏底层，高风险 |
| editTeamMember | 编辑内部团队成员 | - |

### 数据删除
| 方法 | 说明 | 风险等级 |
|------|------|----------|
| directDelete | 直接删库 | 极高 |
| batchDelete | 批量直接删除 | 极高 |
| deleteTeamMember | 删除团队成员 | 中 |
| bulkDelete | 批量彻底删除已作废数据 | 高 |
| delete | 彻底删除单条已作废数据 | 高 |

### 数据查询
| 方法 | 说明 |
|------|------|
| find | FQL 查询多条 |
| findOne | FQL 查询单条 |
| findById | 按 ID 查询单条 |
| findByIds | 按 ID 集合查询 |
| select.query | SQL 查询 |
| select.stream | 大数据量流式查询 |
| findWithRelated | 关联联查 |
| getTeamMember | 获取团队成员 |

---

**文档维护**: 后续涉及该文档中方法的开发，请引用此文档进行实现。
