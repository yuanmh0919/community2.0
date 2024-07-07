package com.yuanmh.community.entity;

/**
 * @Author: Yuanmh
 * @Date: 下午4:55 2024/6/18
 * @Describe: 分页组件
 */

public class Page {
    //当前页码
    private Integer current = 1;
    //页码上限 初始值设置10
    private Integer limit = 10;
    //数据总数 用于计算总页数
    private Integer rows;
    //查询路径 复用分页链接
    private String path;

    public Integer getCurrent() {
        return current;
    }

    //设置当前页码需要做出判断
    public void setCurrent(Integer current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        //设置最大上限100页
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     *
     * @return 起始行
     */
    public int getOffSet() {
        //当前页码减1乘以每页显示条数
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getTotal() {
        //向上取整
        return (int) Math.ceil((double) rows / limit);
    }

    /**
     * 获取起始页码
     */
    public int getFrom() {
        return Math.max(current - 2, 1);
    }

    /**
     * 获取结束页码
     *
     * @return
     */
    public int getTo() {
        return Math.min(current + 2, getTotal());
    }


}
