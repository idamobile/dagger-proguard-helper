package com.idamobile.dagger.helper.sample;

import javax.inject.Inject;

public class ClassWithInjects3 {

    @Inject
    InjectedObj injectedObj;

    @Inject
    InjectedObj2 injectedObj2;

    @Inject
    InjectedObj3<ObjWithoutInjections> injectedObj3;

}
