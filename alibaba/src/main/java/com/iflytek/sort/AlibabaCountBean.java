package com.iflytek.sort;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 阿里巴巴计数豆类，用于存储和比较收藏和购买数量。
 * 实现WritableComparable接口，以便在Hadoop作业中作为键使用。
 */
public class AlibabaCountBean implements WritableComparable<AlibabaCountBean> {

    private long collection;//收藏量
    private long purchase;//购买量

    /**
     * 默认构造函数。
     */
    public AlibabaCountBean() {
    }

    /**
     * 构造函数，初始化收藏和购买数量。
     *
     * @param collection 收藏数量。
     * @param purchase 购买数量。
     */
    public AlibabaCountBean(long collection, long purchase) {
        this.collection = collection;
        this.purchase = purchase;
    }

    /**
     * 设置收藏和购买数量。
     *
     * @param collection 收藏数量。
     * @param purchase 购买数量。
     */
    public void set(long collection, long purchase) {
        this.collection = collection;
        this.purchase = purchase;
    }

    /**
     * 获取收藏数量。
     *
     * @return 收藏数量。
     */
    public long getCollection() {
        return collection;
    }

    /**
     * 设置收藏数量。
     *
     * @param collection 收藏数量。
     */
    public void setCollection(long collection) {
        this.collection = collection;
    }

    /**
     * 获取购买数量。
     *
     * @return 购买数量。
     */
    public long getPurchase() {
        return purchase;
    }

    /**
     * 设置购买数量。
     *
     * @param purchase 购买数量。
     */
    public void setPurchase(long purchase) {
        this.purchase = purchase;
    }

    /**
     * 将AlibabaCountBean实例写入DataOutput流。
     *
     * @param out 数据输出流。
     * @throws IOException 如果写入操作失败。
     */
    /**
     *  序列化方法
     */
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(collection);
        out.writeLong(purchase);
    }

    /**
     * 从DataInput流读取AlibabaCountBean实例的字段。
     *
     * @param in 数据输入流。
     * @throws IOException 如果读取操作失败。
     */
    /**
     * 反序列化方法 注意顺序
     */
    @Override
    public void readFields(DataInput in) throws IOException {
        this.collection = in.readLong();
        this.purchase =in.readLong();
    }

    /**
     * 返回收藏和购买数量的字符串表示。
     *
     * @return 字符串表示。
     */
    @Override
    public String toString() {
        return  collection +"\t"+ purchase;
    }

    /**
     * 比较两个AlibabaCountBean实例的购买数量，用于排序。
     *
     * @param o 另一个AlibabaCountBean实例。
     * @return 比较结果的整数。
     */
    /**
     * 排序比较器
     */
    @Override
    public int compareTo(AlibabaCountBean o) {
        return this.purchase - o.getPurchase()> 0 ? -1:(this.purchase - o.getPurchase() < 0 ? 1 : 0);
        //按照购买量降序排序
    }
}
