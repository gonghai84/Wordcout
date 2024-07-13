import os
from pyecharts import options as opts
from pyecharts.charts import Bar

# 读取文件并解析数据
filename = '收藏量购买量总和.txt'
data = []
with open(filename, 'r') as file:
    for line in file:
        parts = line.strip().split()
        data.append([float(part) for part in parts])
# 将数据拆分成三个列表
x_data = [row[0] for row in data]
y1_data = [row[1] for row in data]
y2_data = [row[2] for row in data]
# 创建柱状图
bar = Bar()
bar.add_xaxis(x_data)
bar.add_yaxis("商品收藏量", y1_data, itemstyle_opts=opts.ItemStyleOpts(color='blue'))
bar.add_yaxis("商品购买量", y2_data, itemstyle_opts=opts.ItemStyleOpts(color='red'))
# 设置图表的标题和其他配置
bar.set_global_opts(
    title_opts=opts.TitleOpts(title="商品收藏量购买量"),
    tooltip_opts=opts.TooltipOpts(trigger="axis"),
    xaxis_opts=opts.AxisOpts(type_="category"),
    yaxis_opts=opts.AxisOpts(type_="value"),
    legend_opts=opts.LegendOpts(pos_top="5%")
)
# 保存图表为 HTML 文件
html_file = '商品收藏量和购买量排序.html'
bar.render(html_file)
print(f"HTML file saved as {html_file}")
