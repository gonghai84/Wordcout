# 导入pandas库，用于数据处理
import pandas as pd

# 从csv文件中读取数据，命名为df
# 加载数据
df = pd.read_csv('taobao.csv')
# 打印数据框架的信息，包括列名、数据类型和非空值数量
# 检查数据信息，包括每列的非空值数量和数据类型
print(df.info())
# 计算每列的缺失值数量并打印
# 检查每列的缺失值数量
print(df.isnull().sum())
# 删除包含缺失值的行
# 删除有缺失值的行（或者选择填充缺失值）
df = df.dropna()
# 打印处理后的数据框架的前5行
# 最后，查看清洗后的数据
print(df.head())
# 从已清洗的csv文件中读取数据，命名为df2
df.to_csv('已清洗数据.csv', index=False)
import pandas as pd
# 读取 CSV 文件
df = pd.read_csv('已清洗数据.csv')
# 定义映射关系
#将CSV文件中的 behavior_type 列中的数值 (1, 2, 3, 4) 转换为相应的文本标签 (点赞、收藏、购物车、支付)
behavior_map = {
    1: '点赞',
    2: '收藏',
    3: '购物车',
    4: '支付'
}
# 将 behavior_type 列中的数值转换为对应的文本
df['behavior_type'] = df['behavior_type'].map(behavior_map)
# 保存修改后的 DataFrame 到新的 CSV 文件
df.to_csv('已清洗数据.csv', index=False)
#输出
print("已清洗数据.csv")

