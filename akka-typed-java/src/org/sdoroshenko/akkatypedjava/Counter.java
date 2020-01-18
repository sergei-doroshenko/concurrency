package org.sdoroshenko.akkatypedjava;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Counter extends AbstractBehavior<Counter.Message> {

    interface Message {
    }

    public static final class Inc implements Message {
        public final int value;

        public Inc(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static final class Print implements Message {
    }

    public static Behavior<Message> create(int value) {
        return Behaviors.setup(ctx -> new Counter(ctx, value));
    }

    private final int value;

    private Counter(ActorContext<Message> context, int value) {
        super(context);
        this.value = value;
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(Inc.class, inc -> {
                    getContext().getLog().info(String.format("Inc: %d", inc.getValue()));
                    return create(value + inc.getValue());
                })
                .onMessage(Print.class, print -> {
                    getContext().getLog().info(String.format("Count: %d", value));
                    return Behaviors.same();
                })
                .onAnyMessage(o -> Behaviors.same())
                .build();
    }

    public static void main(String[] args) throws InterruptedException {
        ActorSystem<Message> system = ActorSystem.create(Counter.create(0), "counter");

        ActorRef<Message> actor = system;

        actor.tell(new Inc(1));
        actor.tell(new Inc(3));
        actor.tell(new Inc(1));
        actor.tell(new Inc(100));
        actor.tell(new Print());

        Thread.sleep(1000);

        system.terminate();
    }
}
