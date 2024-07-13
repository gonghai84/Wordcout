/**
 * 继承自Hadoop的Reducer类，用于对Map阶段输出的数据进行归约操作。
 * 该类用于计算每个键（例如商品ID）对应的收藏总数和购买总数。
 */
package com.iflytek.alibaba;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class AlibabaSumReducer extends Reducer<Text, AlibabaCountBean, Text, AlibabaCountBean> {
    // 用于存储输出的累加结果
    AlibabaCountBean outValue = new AlibabaCountBean();

    /**
     * 在Reduce阶段，对每个键的值（收藏数和购买数）进行累加，然后输出。
     *
     * @param key    Map输出的键，这里表示商品ID。
     * @param values Map输出的值的集合，这里表示同一个商品ID对应的多个AlibabaCountBean对象。。
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void reduce(Text key, Iterable<AlibabaCountBean> values, Context context) throws IOException, InterruptedException {
        // 初始化总收藏数和总购买数
        long totalCollection = 0;
        long totalPurchase = 0;
        // 遍历values集合，对每个AlibabaCountBean对象的收藏数和购买数进行累加
        // 累加统计
        for (AlibabaCountBean value : values) {
            totalCollection += value.getCollection();
            totalPurchase += value.getPurchase();
        }
        // 将累加结果设置到outValue对象中
        outValue.set(totalCollection, totalPurchase);
        // 将键值对写入到上下文中，最终输出
        context.write(key, outValue);
    }
}
