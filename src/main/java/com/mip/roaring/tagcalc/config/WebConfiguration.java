package com.mip.roaring.tagcalc.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 总配置类
 * Created by chen.ni on 2018/3/23.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * 跨域访问配置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//配置可以跨域的路径
                .allowedMethods("*")//配置可以跨域的方法
                .allowedOrigins("*")//配置可以跨域访问的域名，TODO:后面需要改成我们自己的前端路径
                .allowedHeaders("*");//配置可以跨域访问的请求头
    }

    /**
     * FastJson转换器过滤配置，把内部的json转为FastJson
     * fastJson配置实体调用setSerializerFeatures方法可以配置多个过滤方式
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建fastJson消息转换器
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //创建配置类
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //修改配置返回内容的过滤
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.DisableCircularReferenceDetect,//消除对同一对象循环引用的问题，默认为false（如果不配置有可能会进入死循环）
                SerializerFeature.WriteMapNullValue,//是否输出值为null的字段,默认为false。
                SerializerFeature.WriteNullStringAsEmpty, //字符类型字段如果为null,输出为"",而非null
                SerializerFeature.WriteDateUseDateFormat
        );
        fastJsonConfig.setDateFormat("YYYY-MM-dd HH:mm:ss");
        //解决中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);

        fastConverter.setFastJsonConfig(fastJsonConfig);
        //将fastJson添加到视图转换器列表
        converters.add(fastConverter);
    }


}
