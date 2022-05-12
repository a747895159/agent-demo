package com.person.zb.buddy.study;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
public class AgentInterceptor {

    /**
     * @SuperCall 注解注入的 Callable 参数来调用目标方法时，是无法动态修改参数的，如果想要动态修改参数，则需要用到
     * @Morph 注解以及一些绑定操作,
     * .intercept(MethodDelegation.withDefaultConfiguration().
     * withBinders(Morph.Binder.install(MorphingCallable.class)).to(X.class)
     */
    @RuntimeType
    public static Object interceptor(@This Object proxy, @AllArguments Object[] allArguments, @Origin Method method,
                                     @SuperCall Callable<?> callable, @Morph AgentCallable<?> overrideCallable) throws Exception {
        long start = System.currentTimeMillis();
        if (Objects.nonNull(allArguments)) {
            log.info("织入的 请求参数：{}", JSONObject.toJSONString(allArguments));
        }
        try {
            Object o = null;
            if (Objects.nonNull(allArguments)) {
                //可以修改参数： allArguments[1] = "cold";
                //修改参数
//                allArguments[1] = "cold";
                //调用目标方法 可修改参数形式的 调用
                o = overrideCallable.call(allArguments);
            } else {
                //直接调用目标方法
                o = callable.call();
            }
            if (Objects.nonNull(o)) {
                log.info("织入的 返回参数：{}", JSONObject.toJSONString(o));
            } else {
                log.info("织入的方法无返回数据");
            }
            return o;
        } finally {
            log.info("织入的方法:{},总耗时 ：{}", method.getName(), (System.currentTimeMillis() - start));
        }
    }
}