package com.iecmp.commonm.service;

import com.iecmp.commonm.vo.CaptchaVO;
import com.iecmp.commonm.vo.HealthVO;

/**
 * 公共验证码与服务健康检查。
 */
public interface CommonSystemService {

    /** 8-13 生成图形验证码。 */
    CaptchaVO generateCaptcha(String ip);

    /** 8-14 服务健康检查。 */
    HealthVO health();
}