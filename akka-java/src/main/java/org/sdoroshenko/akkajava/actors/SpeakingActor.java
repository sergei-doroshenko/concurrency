package org.sdoroshenko.akkajava.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SpeakingActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, s -> {
                log.info("Got string message: {}", s);
            })
            .match(Integer.class, i -> {
                log.info("Got integer message: {}", i);
            })
            .matchAny(o -> log.warning("Unknown message"))
            .build();
    }
}
