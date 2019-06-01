package com.mip.roaring.tagcalc.velocity.controller;

import com.mip.roaring.tagcalc.velocity.service.CalculatorService;
import com.mip.roaring.tagcalc.velocity.utils.Infix2SuffixCalculator;
import com.mip.roaring.tagcalc.velocity.vo.OperateResultVo;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author ZhengWenbiao
 * @Date 2019/5/31 15:42
 **/
@Controller
@CrossOrigin
@Log4j2
public class CalculatorController {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    @Resource
    private CalculatorService calculatorService;

    @ResponseBody
    @RequestMapping(value = "/createTag", method = {RequestMethod.POST, RequestMethod.GET})
    public OperateResultVo createTag(@RequestParam(value = "id") Long id,
                                     @RequestParam("data") List<String> reqData) {
        OperateResultVo vo = new OperateResultVo();
        try {
            if (reqData.isEmpty()) {
                vo.setMsg("empty data,please reload");
                vo.setSuccess(false);
                return vo;
            }
            return calculatorService.saveAudience(id, reqData);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("inner service error");
            vo.setMsg("inner service error");
            vo.setSuccess(false);
            return vo;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/calculatorTag", method = {RequestMethod.POST, RequestMethod.GET})
    public OperateResultVo calculatorTag(@RequestParam(value = "expression") String ids,
                                         @RequestParam(value = "id") List<Long> list) {
        OperateResultVo vo = new OperateResultVo();
        if (ids == null) {
            vo.setSuccess(false);
            vo.setMsg("传入参数错误！negative error params");
            return vo;
        }
        String tagId;
        try {
            tagId = Infix2SuffixCalculator.regexNumber(ids);
            if (tagId == null || tagId.isEmpty()) {
                vo.setSuccess(false);
                vo.setMsg("传入参数错误！negative error params");
                return vo;
            }
            return calculatorService.handleRoaringId(tagId,list);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("calculator tags error");
            vo.setMsg("calculator tags error");
            vo.setSuccess(false);
            return vo;
        }
    }
}
