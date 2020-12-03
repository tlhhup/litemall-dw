use litemall;
-- 清除用户表
delete from litemall_user where id >1;
-- 清除地址表
truncate table litemall_address;
-- 清除加购表
truncate table litemall_cart;
-- 清除收藏表
truncate table litemall_collect;
-- 清除评论表
truncate table litemall_comment;
-- 清除领劵表
truncate table litemall_coupon_user;
-- 清除团购订单表
truncate table litemall_groupon;
-- 清除订单表
truncate table litemall_order;
-- 清除订单详情表
truncate table litemall_order_goods;