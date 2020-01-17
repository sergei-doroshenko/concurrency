package org.sdoroshenko.akkajava.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class OutspokenActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public void preStart() throws Exception, Exception {
        super.preStart();
        log.info("Actor was born: {}", this.self());
    }

    @Override
    public void postStop() throws Exception, Exception {
        super.postStop();
        log.info("Actor was died: {}", this.self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, s -> {
                log.info("Got string message: {} from: {}", s, this.sender());
                if (!s.startsWith("Echo")) {
                    this.getSender().tell("Echo: " + s, this.self());
                }
            })
            .matchAny(o -> log.warning("Unknown message"))
            .build();
    }
}
