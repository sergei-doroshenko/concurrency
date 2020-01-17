package org.sdoroshenko.akkajava;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.sdoroshenko.akkajava.actors.Accumulator;
import org.sdoroshenko.akkajava.actors.OutspokenActor;
import scala.compat.java8.FutureConverters;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
//import static akka.pattern.Patterns.ask;
//import static akka.pattern.Patterns.pipe;

public class AkkaSample {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // load config from application.conf
//        Config config = ConfigFactory.load();
        ActorSystem system = ActorSystem.create("Theater");
        // Prints all system settings
//        System.out.println(system.settings());

        Props props = Props.create(Accumulator.class)
            /*.withDispatcher("akka.accumulator.custom-dispatcher")*/;
        ActorRef accumulator = system.actorOf(props, "Accumulator");

        System.out.println(accumulator.toString()); // Actor[akka://Theater/user/Makbet#1691857855]
        System.out.println(accumulator.isTerminated());

        accumulator.tell(1, ActorRef.noSender());
        accumulator.tell(10, ActorRef.noSender());
        accumulator.tell(-3, ActorRef.noSender());
        accumulator.tell(Accumulator.GET_SUM, null);

        ActorRef romeo = system.actorOf(Props.create(OutspokenActor.class), "Romeo");
        ActorRef juliet = system.actorOf(Props.create(OutspokenActor.class), "Juliet");

        romeo.tell("Hi, Romeo!", juliet);
        juliet.tell("How are you, Juliet!", romeo);

        // Mailboxes
        /*final Inbox julietInbox = Inbox.create(system);
        final Inbox romeoInbox = Inbox.create(system);

        julietInbox.send(juliet, "Hello, Rommy!");
        julietInbox.send(juliet, "Where are you?");

        romeoInbox.send(romeo, "Hey, Jul!");
        romeoInbox.send(romeo, "I'm here.");

        System.out.println("Message for Juliet: " + julietInbox.receive(Duration.create(1, TimeUnit.SECONDS)));

        romeoInbox.watch(romeo);
        julietInbox.watch(juliet);*/

        romeo.tell(PoisonPill.getInstance(), ActorRef.noSender());
        juliet.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }
}
