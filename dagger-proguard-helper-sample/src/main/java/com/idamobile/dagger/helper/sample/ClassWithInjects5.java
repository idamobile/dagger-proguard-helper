package com.idamobile.dagger.helper.sample;

import javax.inject.Inject;

public class ClassWithInjects5 extends ClassWithInjects {

    public static class InnerStaticClass {
        @Inject
        InjectedObj injectedObj;
    }

    public static class InnerClass {
        @Inject
        InjectedObj2 injectedObj;
    }

}