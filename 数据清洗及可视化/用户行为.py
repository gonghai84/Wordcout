from pyecharts.charts import Bar
from pyecharts import options as opts
# 读取文件内容
data = []
with open('用户行为.txt', 'r', encoding='utf-8') as file:
    for line in file:
        parts = line.strip().split()
        if len(parts) == 2:
            data.append((parts[0], int(parts[1])))
# 分离横纵坐标数据
x_data = [item[0] for item in data]
y_data = [item[1] for item in data]
# 创建柱状图
bar = Bar()
bar.add_xaxis(x_data)
bar.add_yaxis("用户行为统计", y_data)
# 设置全局配置项
bar.set_global_opts(
    title_opts=opts.TitleOpts(title="用户行为分析"),
    xaxis_opts=opts.AxisOpts(name="购买行为"),
    yaxis_opts=opts.AxisOpts(name="数量"),
)
# 渲染图表到HTML文件
bar.render('用户行为.html')
