package com.person.zb.buddy.study;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;

/***
 * 测试Demo
 *
 * Skywalking 学习指南:   https://blog.csdn.net/qq_18515155/article/details/114454166
 */
public class Agent002 {
    public static void premain(String arguments, Instrumentation instrumentation) {

        AgentBuilder.Transformer transformer = new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                return builder.method(nameStartsWith("test"))
                        .intercept(MethodDelegation.withDefaultConfiguration()
                                .withBinders(Morph.Binder.install(AgentCallable.class))
                                .to(AgentInterceptor.class));
            }
        };
        new AgentBuilder.Default()
                .type(named("com.yonghui.wms.dc.ibd.makeup.controller.manual.ManualController"))
                .transform(transformer).installOn(instrumentation);
    }
}
