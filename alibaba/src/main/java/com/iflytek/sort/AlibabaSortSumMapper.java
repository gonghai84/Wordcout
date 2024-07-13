/**
 * 自定义映射类，用于阿里巴巴排序汇总任务。
 * 继承自Hadoop的Mapper类，针对LongWritable类型的键和Text类型的值进行映射处理，
 * 输出AlibabaCountBean类型的键和Text类型的值。
 */
package com.iflytek.sort;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AlibabaSortSumMapper extends Mapper<LongWritable, Text, AlibabaCountBean, Text> {

    // 定义输出键值对的实例变量
    AlibabaCountBean outKey = new AlibabaCountBean();
    Text outValue = new Text();

    /**
     * 映射函数，对输入的每行文本进行处理，并生成相应的输出键值对。
     * @param key   输入键，通常是行的起始位置
     * @param value 输入值，通常是行的文本内容
     * @param context 用于写入输出键值对的上下文
     * @throws IOException 如果处理过程中发生IO异常
     * @throws InterruptedException 如果处理过程中被中断
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 分割输入值（文本行）以获取字段
        String[] fields = value.toString().split("\t");

        // 设置输出键的集合和购买数量字段
        outKey.setCollection(Long.parseLong(fields[1]));
        outKey.setPurchase(Long.parseLong(fields[2]));

        // 设置输出值为第一字段
        outValue.set(fields[0]);

        // 写入输出键值对
        context.write(outKey, outValue);
    }
}
