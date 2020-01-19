package org.sdoroshenko.pcassembly;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class AssemblyActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public void preStart() throws Exception {
        super.preStart();
        log.info("Actor was born: {}", this.self());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        log.info("Actor was died: {}", this.self());
    }

    private Command getCommand() {
        return Command.InstallCPU;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .matchEquals(getCommand(), command -> {
                log.info("Got command: {} from: {}", command, this.sender());
            })
            .matchAny(o -> log.warning("Unknown message"))
            .build();
    }


}
