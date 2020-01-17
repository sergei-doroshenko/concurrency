package org.sdoroshenko.akkajava.actors;

import akka.actor.Actor;
import akka.actor.UntypedAbstractActor;

public class EmptyActor extends UntypedAbstractActor {

    @Override
    public void onReceive(Object message) throws Throwable, Throwable {
        // do nothing
    }
}
