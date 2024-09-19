package com.test.interceptor;

import com.test.utils.IpUtils;
import com.test.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Slf4j
public class IpUrlLimitInterceptor implements HandlerInterceptor {

    @Resource
    RedisUtils redisUtils;

    private static  final  String LOCK_IP_URL_KEY="lock_ip_";

    private static  final String IP_URL_REQ_TIME="ip_url_times_";
    //访问次数限制
    private static final long LIMIT_TIMES=5;

    //限制时间 秒为单位
    private static final int IP_LOCK_TIME=300;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("request请求地址uri={},ip={}",request.getRequestURI(), IpUtils.getRequestIP(request));
        if(ipIsLock(IpUtils.getRequestIP(request))){
            log.info("ip访问被禁止={}",IpUtils.getRequestIP(request));
//            throw  new Exception("当前操作过于频繁，请5分钟后重试");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("ip访问被禁止");
            writer.flush();
            writer.close();
            return false;
        }
        if (!addRequestTime(IpUtils.getRequestIP(request),request.getRequestURI())){
            log.info("当前{}操作过于频繁，请5分钟后重试",IpUtils.getRequestIP(request));
//            throw  new Exception("当前操作过于频繁，请5分钟后重试");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("当前操作过于频繁，请5分钟后重试");
            writer.flush();
            writer.close();
            return false;
        }
        return true;
    }

    private boolean addRequestTime(String ip, String uri) {
        String key = IP_URL_REQ_TIME+ip+uri;
        if(redisUtils.hasKey(key)){
            long time=redisUtils.incr(key, 1L);
            if(time >=LIMIT_TIMES){
                redisUtils.set(LOCK_IP_URL_KEY+ip,IP_LOCK_TIME);
                return false;
            }
        }else {
            boolean set = redisUtils.set(key,  1L, 1);
        }
        return  true;
    }

    private boolean ipIsLock(String ip) {
        if(redisUtils.hasKey(LOCK_IP_URL_KEY+ip)){
            return true;
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
