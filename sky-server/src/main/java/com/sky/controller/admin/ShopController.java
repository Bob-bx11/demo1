package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺状态管理
 */
@RestController
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    // 营业状态：1营业 0打烊
    public static final Integer OPEN = 1;
    public static final Integer CLOSE = 0;

    /**
     * 查询店铺营业状态
     */
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus() {
        // 简单写死，后面可以改到 Redis/数据库
        return Result.success(OPEN);
    }
}