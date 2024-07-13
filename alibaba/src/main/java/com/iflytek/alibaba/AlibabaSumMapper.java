package com.iflytek.alibaba;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * AlibabaSumMapper类是MapReduce作业中的Mapper部分，负责处理输入数据并生成中间键值对。
 * 它继承自Mapper类，具体实现了map方法来解析输入行并生成输出。
 * 输入键是LongWritable，表示行的偏移量；输入值是Text，表示一行文本。
 * 输出键是Text，表示商品类别；输出值是AlibabaCountBean，表示商品的收藏量和购买量。
 */
public class AlibabaSumMapper extends Mapper<LongWritable, Text, Text, AlibabaCountBean> {

    // 初始化输出键和输出值对象
    Text outKey = new Text();
    AlibabaCountBean outValue = new AlibabaCountBean();

    /**
     * map方法用于处理每个输入键值对，并生成相应的中间键值对。
     * 它是Mapper类的抽象方法，需要在子类中实现。
     *
     * @param key   输入键，通常是行的偏移量。
     * @param value 输入值，一行文本。
     * @param context 用于写入输出键值对的上下文对象。
     * @throws IOException        如果发生I/O错误。
     * @throws InterruptedException 如果线程被中断。
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 分割输入值（文本行）以获取字段
        String[] fields = value.toString().split(",");
        // 设置输出键为商品类别
        // 商品种类
        outKey.set(fields[1]);
        // 设置输出值为收藏量和购买量
        // Alibaba数据 收藏量 购买量
        outValue.set(Long.parseLong(fields[fields.length - 2]), Long.parseLong(fields[fields.length - 1]));
        // 写出中间键值对
        // map输出结果
        context.write(outKey, outValue);
    }
}
