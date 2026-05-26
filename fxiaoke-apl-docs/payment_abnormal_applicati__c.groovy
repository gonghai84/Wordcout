/**
 * @author 纷享管理员
 * @codeName 【二期】【款项异动申请】款项异动申请限制产品名称为代理商明细
 * @description 【二期】【款项异动申请】款项异动申请限制产品名称为代理商明细
 * @createTime 2025-11-27
 * @bindingObjectLabel 款项异动申请
 * @bindingObjectApiName payment_abnormal_applicati__c
 * @函数需求编号
 */
String agent__c = context.data.agent__c as String
log.info("客户名称==" + agent__c)

String sql = "SELECT _id, field_fQGav__c FROM object_s3Kdx__c WHERE field_K76mn__c = '${agent__c}'"
SelectAttribute attribute = SelectAttribute.builder().needInvalid(false).build()

def selectResult = Fx.object.select(sql, attribute)
if (selectResult.isError()) {
    log.info(selectResult.message())
    return []
}

QueryResult result = selectResult.result() as QueryResult
log.info("result==" + result)

List objectIds = []
result.dataList.each { item ->
    Map map = item as Map
    objectIds.add(map.field_fQGav__c)
}

log.info("objectIds==" + objectIds)
return objectIds
