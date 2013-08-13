package com.idamobile.dagger.helper.sample;

import dagger.Provides;

@dagger.Module(complete = false, injects = {
        ClassWithInjects.class,
        ClassWithInjects2.class,
        ClassWithInjects3.class,
        ClassWithInjects4.class,
        ParentWithInjections.class,
})
public class SubModule {
    @Provides
    String getName() {
        return null;
    }

    @Provides
    InjectedObj3<ObjWithoutInjections> getInjectedObj4() {
        return null;
    }

    @Provides
    Interface anInterface() {
        return new InterfaceImpl();
    }
}
