package org.sdoroshenko.akkajava.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Accumulator extends AbstractActor {
    public static final String GET_SUM = "get sum";
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private int sum;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, s -> {
                if (GET_SUM.equals(s)) {
                    log.info("The sum: {}", sum);
                }
            })
            .match(Integer.class, i -> {
                log.info("Got integer message: {}", i);
                sum += i;
            })
            .matchAny(o -> log.warning("Unknown message"))
            .build();
    }
}
