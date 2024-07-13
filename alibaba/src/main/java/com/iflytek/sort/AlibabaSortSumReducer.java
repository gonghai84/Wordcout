/**
 * 继承自Reducer类，用于对Map阶段输出的数据进行Reduce操作。
 * 本类用于实现商品销售数据的排序和汇总功能。
 * 
 * @param <AlibabaCountBean> Map输出的键值对类型，包含商品信息和计数。
 * @param <Text> Hadoop的Text类，用于表示文本数据。
 */
package com.iflytek.sort;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class AlibabaSortSumReducer extends Reducer<AlibabaCountBean, Text, Text, AlibabaCountBean> {
    /**
     * Reduce方法用于处理Map阶段输出的键值对集合，进行聚合操作。
     * 在本方法中，我们通过遍历values迭代器，取出第一个元素作为输出的key，
     * 并将key作为输出的value，用于后续的排序和汇总。
     * 
     * @param key Map输出的键，包含商品信息。
     * @param values Map输出的值的迭代器，包含对应商品的计数信息。
     * @param context 用于写入输出数据的上下文对象。
     * @throws IOException 如果发生I/O错误。
     * @throws InterruptedException 如果线程被中断。
     */
    @Override
    protected void reduce(AlibabaCountBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // 读取到商品种类
        Text outKey = values.iterator().next();
        context.write(outKey, key);
    }
}
