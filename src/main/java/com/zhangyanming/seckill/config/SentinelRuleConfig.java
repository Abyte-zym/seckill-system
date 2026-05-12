package com.zhangyanming.seckill.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import java.util.*;

/**
 * Sentinel限流熔断规则配置
 */
@Configuration
public class SentinelRuleConfig {

    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // 秒杀接口限流：QPS 1000
        FlowRule seckillRule = new FlowRule();
        seckillRule.setResource("seckill-order");
        seckillRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        seckillRule.setCount(1000);
        rules.add(seckillRule);

        FlowRuleManager.loadRules(rules);
    }
}
