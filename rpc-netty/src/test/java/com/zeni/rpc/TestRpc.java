package com.zeni.rpc;

import org.junit.Test;


public class TestRpc {

    @Test
    public void Test(){
        String canonicalName = this.getClass().getCanonicalName();
        System.out.println(canonicalName);
    }
}
