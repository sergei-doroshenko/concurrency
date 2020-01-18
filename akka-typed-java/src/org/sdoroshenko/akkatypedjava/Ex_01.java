package org.sdoroshenko.akkatypedjava;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class Ex_01 {

    public static void main(String[] args) throws InterruptedException {


        Behavior<String> greeter = Behaviors.receive((context, message) -> {
            context.getLog().info(String.format("Hello %s!", message));
            return Behaviors.same();
        });

        ActorSystem<String> system = ActorSystem.create(greeter, "greeter");

        ActorRef<String> actor = system;

        actor.tell("World");

        Thread.sleep(1000);

        system.terminate();
    }
}
