package com.iflytek.alibaba;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AlibabaSumDriver {
    public static void main(String[] args) throws Exception{
        String inputPath = "hdfs://localhost:9000/taobao/input/spe_col_purchase.dat";
        String outputPath = "datas/alibaba/output";
        //配置文件对象
        Configuration conf = new Configuration();
        // 创建作业实例
        Job job = Job.getInstance(conf, AlibabaSumDriver.class.getSimpleName());
        // 设置作业驱动类
        job.setJarByClass(AlibabaSumDriver.class);
        // 设置作业mapper reducer类
        job.setMapperClass(AlibabaSumMapper.class);
        job.setReducerClass(AlibabaSumReducer.class);
        // 设置作业mapper阶段输出key value数据类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(AlibabaCountBean.class);
        //设置作业reducer阶段输出key value数据类型 也就是程序最终输出数据类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(AlibabaCountBean.class);
        // 配置作业的输入数据路径
        FileInputFormat.addInputPath(job, new Path(inputPath));
        // 配置作业的输出数据路径
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        //判断输出路径是否存在 如果存在删除
        FileSystem fs = FileSystem.get(conf);
        if(fs.exists(new Path(outputPath))){
            fs.delete(new Path(outputPath),true);
        }
        // 提交作业并等待执行完成
        boolean resultFlag = job.waitForCompletion(true);
        //程序退出
        System.exit(resultFlag ? 0 :1);
    }
}
