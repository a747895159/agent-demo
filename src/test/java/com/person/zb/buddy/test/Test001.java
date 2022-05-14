package com.person.zb.buddy.test;

import com.person.zb.buddy.study.util.DemoUtil;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @Desc: ByteBuddy入门级案例指南  参考：https://zhuanlan.zhihu.com/p/151843984
 * 官网教程：https://bytebuddy.net/#/tutorial
 * @Author: ZhouBin
 * @Date: 2022/5/10
 */
public class Test001 {

    /**
     * 虽然我们可以动态创建类，我们也可以操作已经加载的类。
     * ByteBuddy可以重定义已经存在的类，然后使用ByteBuddyAgent将重定义的类重新加载到JVM中。
     */
    @Test
    public void defineClass() throws Exception {

        ByteBuddyAgent.install();
        DynamicType.Loaded<Foo> load = new ByteBuddy()
                .redefine(Foo.class)
                .method(named("sayHelloFoo"))
                .intercept(FixedValue.value("Hello Foo Redefined"))
                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        DemoUtil.writeAndNew(load);

        Foo foo = new Foo();

        System.out.println(foo.sayHelloFoo());


    }

    /**
     * 运行时创建一个类，添加方法和字段定义、实现相关接口
     */
    @Test
    public void addMethodAndAttr() throws Exception {
        DynamicType.Loaded<Object> load = new ByteBuddy()
                .subclass(Object.class)
                .name("MyClassName")
                .defineMethod("custom", String.class, Modifier.PUBLIC)
                /**
                 * FixedValue(方法调用返回固定值)
                 * MethodDelegation(方法调用委托,支持两种方式: Class的static方法调用、object的instance method方法调用)
                 */
                .intercept(MethodDelegation.to(Bar.class))
                // 新增一个字段,字段名字为name,类型为String,且public修饰
                .defineField("name", String.class, Modifier.PUBLIC)
                .implement(BarInterface.class) //实现DemoInterface接口
                //实现接口的方式是读写name字段
                .intercept(FieldAccessor.ofField("name"))
                .make()
                .load(Test001.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);

        DemoUtil.write(load);
        Class<?> type = load.getLoaded();

        Method m = type.getDeclaredMethod("custom", null);
        System.out.println(m.invoke(type.newInstance()));
        System.out.println(Bar.sayHelloBar());
        System.out.println(type.getDeclaredField("name"));
    }

    /**
     * 方法代理和自定义方法逻辑
     */
    @Test
    public void execProxyChange() throws Exception {
        /**
         * ByteBuddy怎么知道该调用Bar.class中的哪个方法？
         * ByteBuddy根据方法签名、返回值类型、方法名、注解的顺序来匹配方法（越后面的优先级越高）。
         * sayHelloFoo()方法和sayHelloBar()方法的方法名不一样，但是它们有相同的方法签名和返回值类型。
         *
         * 如果在Bar.class中有超过一个可调用的方法的入参和返回类型一致，我们可以使用@BindingPriority来解决冲突。
         * @BindingPriority有一个整型参数-这个值越大优先级越高。
         */
        Foo foo = new ByteBuddy()
                .subclass(Foo.class)
                // 匹配由 Foo.class声明的方法 且方法名sayHelloFoo 且返回类型是String的方法
                .method(named("sayHelloFoo").and(isDeclaredBy(Foo.class).and(returns(String.class))))
                //方法调用也可以委托给字段（而非外部对象）：intercept(MethodDelegation.toField("name"))
                .intercept(MethodDelegation.to(Bar.class))
                .make()
                .load(Test001.class.getClassLoader())
                .getLoaded()
                .newInstance();


        System.out.println(foo.sayHelloFoo());
        Foo foo2 = new Foo();
        System.out.println(foo2.sayHelloFoo());
    }


    /**
     * 运行时创建java子类 并重写 toString方法
     */
    @Test
    public void createNewClass() throws Exception {
        //运行时创建java类
        DynamicType.Unloaded unloadedType = new ByteBuddy()
                //含义是动态创建的类是继承Foot类的
                .subclass(Object.class)
                //类似一个筛选器，这里选中的是Object类中的toString()方法
                .method(ElementMatchers.isToString())
                //提供了了toString()的实现，这里的实现是返回一个固定的值"Hello World ByteBuddy!"
                .intercept(FixedValue.value("Hello World ByteBuddy!"))
                //触发生成一个新的类
                .make();
        //将生成的类加载到JVM中
        Class<?> dynamicType = unloadedType.load(Test001.class
                .getClassLoader())
                .getLoaded();

        //newInstance()是一个java反射方法用于创建ByteBuddy对象表示的实例；这个方式就类似于使用无参构造函数创建一个对象。
        System.out.println(dynamicType.newInstance().toString());
    }

}
