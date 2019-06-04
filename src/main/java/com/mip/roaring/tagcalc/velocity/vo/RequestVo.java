package com.mip.roaring.tagcalc.velocity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author ZhengWenbiao
 * @Date 2019/6/3 16:55
 **/
@Data
public class RequestVo implements Serializable{

    private static final long serialVersionUID = 8716058742955428646L;

    String expression;
    List<Long> id;
}
