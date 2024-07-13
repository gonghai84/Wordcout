package com.iflytek.alibaba;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * AlibabaCountBean类用于存储和处理收藏和购买数量的数据。
 * 该类实现了Apache Hadoop的Writable接口，以支持数据的序列化和反序列化。
 */
public class AlibabaCountBean implements Writable {

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
     * @param collection 新的收藏数量。
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
     * @param purchase 新的购买数量。
     */
    public void setPurchase(long purchase) {
        this.purchase = purchase;
    }

    /**
     * 将对象序列化到数据输出流。
     *
     * @param out 数据输出流。
     * @throws IOException 如果序列化过程中发生错误。
     */
    /**
     * 序列化方法
     */
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(collection);
        out.writeLong(purchase);
    }

    /**
     * 从数据输入流反序列化对象。
     *
     * @param in 数据输入流。
     * @throws IOException 如果反序列化过程中发生错误。
     */
    /**
     * 反序列化方法 注意顺序
     */
    @Override
    public void readFields(DataInput in) throws IOException {
        this.collection = in.readLong();
        this.purchase = in.readLong();
    }

    /**
     * 返回收藏和购买数量的字符串表示。
     *
     * @return 字符串表示，格式为"收藏量 购买量"。
     */
    @Override
    public String toString() {
        return collection + "\t" + purchase;
    }
}
