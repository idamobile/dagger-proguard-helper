package com.idamobile.dagger.helper.sample;

import javax.inject.Inject;

public class ClassWithInjects {
    @Inject
    InjectedObj injectedObj;

    @Inject
    InjectedObj injectedObj2;

    @Inject
    String name;
}
