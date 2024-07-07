package com.yuanmh.community.dao;

import com.yuanmh.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Author: Yuanmh
 * @Date: 上午8:47 2024/6/20
 * @Describe:
 */
@Mapper
@Deprecated //声明本接口已过时，不再维护
public interface LoginTicketMapper {

    //插入登录票据
    int insertLoginTicket(LoginTicket loginTicket);

    //根据ticket获取登录票据 使用注解方式
    //sql语句较长时，可以说使用逗号分割开 会自动拼接成一个sql语句
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //修改登录票据状态 动态sql如何写？使用<script>标签
    @Update({
            "<script>",
            "UPDATE login_ticket SET status = #{status} WHERE ticket = #{ticket} ",
            "<if test='ticket!=null'> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
