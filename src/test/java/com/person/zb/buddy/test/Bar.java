package com.person.zb.buddy.test;

import net.bytebuddy.implementation.bind.annotation.BindingPriority;

public class Bar {

        @BindingPriority(3)
        public static String sayHelloBar() {
            return "Hello in Bar!";
        }

        @BindingPriority(5)
        public static String sayHello2() {
            return "Bar2!";
        }

        @BindingPriority(1)
        public static String sayHello() {
            return "Bar!";
        }
    }