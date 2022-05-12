package com.person.zb.buddy.study;

public interface AgentCallable<T> {
    T call(Object... arguments);
}