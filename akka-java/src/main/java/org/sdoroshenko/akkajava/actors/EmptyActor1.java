package org.sdoroshenko.akkajava.actors;

import akka.actor.AbstractActor;

public class EmptyActor1 extends AbstractActor {

    @Override
    public Receive createReceive() {
        return null;
    }
}
