
package com.mip.roaring.tagcalc.velocity.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author ZhengWenbiao
 * @Date 2019/5/31 15:39
 **/
@Data
@ToString(callSuper = true)
public class OperateResultVo implements Serializable {

    private static final long serialVersionUID = 7307018197222359131L;
    /**
     * 标识操作是否成功
     */
    private boolean success;

    /**
     * 操作返回的提示信息
     */
    private String msg;

    /**
     * 各个操作的扩展属性
     */
    private Object data;
}
