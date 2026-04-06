package io.liuzhuoran.talentflow.service;

import io.liuzhuoran.talentflow.mapper.PoliticsstatusMapper;
import io.liuzhuoran.talentflow.model.Politicsstatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @作者 江南一点雨
 * @公众号 江南一点雨
 * @微信号 a_java_boy
 * @GitHub https://github.com/liuzhuoran
 * @博客 http://wangsong.blog.csdn.net
 * @网站 https://github.com/liuzhuoran
 * @时间 2019-11-03 23:20
 */
@Service
public class PoliticsstatusService {
    @Autowired
    PoliticsstatusMapper politicsstatusMapper;
    public List<Politicsstatus> getAllPoliticsstatus() {
        return politicsstatusMapper.getAllPoliticsstatus();
    }
}
