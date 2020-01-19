package org.sdoroshenko.akkatypedjava.psassembly;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class AssemblyActor extends AbstractBehavior<PC> {

    public AssemblyActor(ActorContext<PC> context) {
        super(context);
    }

    private Command getCommand() {
        return Command.InstallCPU;
    }

    public static Behavior<PC> create() {
        return Behaviors.setup(ctx -> new AssemblyActor(ctx));
    }

    @Override
    public Receive<PC> createReceive() {
        return newReceiveBuilder()
                .onMessage(PC.class, pc -> {
                    getContext().getLog().info("TEst------------> " + pc);
                    return create();
                })
                .onAnyMessage(o -> Behaviors.same())
                .build();
    }
}
