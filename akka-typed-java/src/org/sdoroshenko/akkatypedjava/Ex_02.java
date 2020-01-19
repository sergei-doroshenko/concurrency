package org.sdoroshenko.akkatypedjava;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.util.function.Function;

public class Ex_02 {
    interface Message {
    }

    static class Inc implements Message {
        private final int value;

        Inc(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    static class Print implements Message {

    }

    static class Counter extends AbstractBehavior<Message> {
        private final int value;
        public Counter(ActorContext<Message> context, int value) {
            super(context);
            this.value = value;
        }

        @Override
        public Receive<Message> createReceive() {
            return ReceiveBuilder.<Message>create()
                    .onMessage(Inc.class, inc -> {
                        getContext().getLog().info("Inc");
                        return new Counter(getContext(), value + inc.getValue());
                    })
                    .onMessage(Print.class, print -> {
                        getContext().getLog().info(String.format("Count: %d", value));
                        return Behaviors.same();
                    })
                    .onAnyMessage(o -> Behaviors.same())
                    .build();
        }
    }

    public static Behavior<Message> counter(int value) {
        System.out.println(value);
        return Behaviors.setup(ctx -> Behaviors.receiveMessage(msg -> BehaviorBuilder.<Message>create()
                .onMessage(Inc.class, inc -> {
                    ctx.getLog().info("Inc");
                    return counter(value + inc.getValue());
                })
                .onMessage(Print.class, print -> {
                    ctx.getLog().info(String.format("Count: %d", value));
                    return Behaviors.same();
                })
                .onAnyMessage(o -> Behaviors.same())
                .build()));
    }

    public static void main(String[] args) throws InterruptedException {

        ActorSystem<Message> system = ActorSystem.create(counter(0), "counter");

        ActorRef<Message> actor = system;

        actor.tell(new Inc(1));
        actor.tell(new Inc(1));
        actor.tell(new Inc(1));
        actor.tell(new Inc(100));
        actor.tell(new Print());

        Thread.sleep(1000);

        system.terminate();
    }
}
