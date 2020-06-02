/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.ruoyi.process.modules.flow.vo;

import java.util.Date;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/27
 */
public class TaskNoticeVO {

    String title;

    String content;

    String assignee;

    /**
     * 待办
     */
    Boolean todo;
    /**
     * 待签收
     */
    Boolean claim;

    Date createTime;

    /**
     * 是否委托
     */
    Boolean delegate;

    public TaskNoticeVO() {
    }

    public TaskNoticeVO(String title, String content, String assignee, Boolean todo, Boolean claim, Date createTime, Boolean delegate) {
        this.title = title;
        this.content = content;
        this.assignee = assignee;
        this.todo = todo;
        this.claim = claim;
        this.createTime = createTime;
        this.delegate = delegate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Boolean getTodo() {
        return todo;
    }

    public void setTodo(Boolean todo) {
        this.todo = todo;
    }

    public Boolean getClaim() {
        return claim;
    }

    public void setClaim(Boolean claim) {
        this.claim = claim;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getDelegate() {
        return delegate;
    }

    public void setDelegate(Boolean delegate) {
        this.delegate = delegate;
    }
}
