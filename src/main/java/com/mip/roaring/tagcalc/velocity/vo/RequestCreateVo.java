package com.mip.roaring.tagcalc.velocity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author ZhengWenbiao
 * @Date 2019/6/4 10:30
 **/
@Data
public class RequestCreateVo implements Serializable {

    private static final long serialVersionUID = 4143741819661529199L;

    private Long id;
    private List<String> data;
}
