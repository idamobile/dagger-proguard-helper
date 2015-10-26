package com.idamobile.dagger.helper.sample;

import dagger.Provides;

@dagger.Module(includes = {
    SubModule.class
}, injects = {
        ClassWithInjects.class,
        ClassWithInjects2.class,
        ClassWithInjects3.class,
        ClassWithInjects4.class,
        ClassWithInjects5.class
})
public class Module {

    @Provides
    InjectedObj getInjectedObj() {
        return null;
    }

    @Provides
    InjectedObj2 getInjectedObj2() {
        return null;
    }
}
