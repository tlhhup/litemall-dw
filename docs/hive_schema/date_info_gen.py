# -*- coding: utf-8 -*-
from datetime import date, timedelta
from chinese_calendar import is_workday, is_in_lieu
import argparse

"""
生成如下格式的数据

CREATE EXTERNAL TABLE `dwd_dim_date_info`(
    `date_id` string COMMENT '日', 
    `week_id` int COMMENT '周',
    `week_day` int COMMENT '周的第几天', 
    `day` int COMMENT '每月的第几天', 
    `month` int COMMENT '第几月', 
    `quarter` int COMMENT '第几季度', 
    `year` int COMMENT '年',
    `is_workday` int COMMENT '是否是周末', 
    `holiday_id` int COMMENT '是否是节假日'
)

2019-01-01	1	3	1	1	1	2019	0	0
"""


def get_quarter(month):
    """
        计算月份对应的季度
    :param month:
    :return:
    """
    quarter = month // 3
    if month % 3 > 0:
        quarter += 1
    return quarter


if __name__ == '__main__':
    # 定义参数解析器，声明需要的参数
    parser = argparse.ArgumentParser(description='Process date info gen.')
    parser.add_argument('start_date', metavar='start_date', help='开始日期，yyyy-MM-dd')
    parser.add_argument('end_date', metavar='end_date', help='结束日期，yyyy-MM-dd')
    # 解析参数
    args = parser.parse_args()

    start_date = date.fromisoformat(args.start_date)
    end_date = date.fromisoformat(args.end_date)

    with open('date_info.txt', mode='w') as f:
        while start_date < end_date:
            date_info = '{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}'.format(start_date,
                                                                                   start_date.isocalendar()[1],
                                                                                   start_date.isoweekday(),
                                                                                   start_date.day,
                                                                                   start_date.month,
                                                                                   get_quarter(start_date.month),
                                                                                   start_date.year,
                                                                                   int(not is_workday(start_date)),
                                                                                   int(is_in_lieu(start_date)))

            print(date_info)
            f.write(date_info)
            f.write('\n')
            # 更新时间
            start_date = start_date + timedelta(days=1)
