package org.sdoroshenko.akkajava;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.sdoroshenko.akkajava.actors.OutspokenActor;
import scala.compat.java8.FutureConverters;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AskPatternSample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ActorSystem system = ActorSystem.create("ROOT");

        ActorRef hamlet = system.actorOf(Props.create(OutspokenActor.class), "Hamlet");
        CompletableFuture<Object> hamletFuture = FutureConverters.toJava(Patterns.ask(hamlet, "To be or not to be", 1000)).toCompletableFuture();
        System.out.println(hamletFuture.get());

        ActorRef ghost = system.actorOf(Props.create(OutspokenActor.class), "Ghost");
        Timeout slowGhost = new Timeout(Duration.create(2, TimeUnit.SECONDS));
        CompletableFuture<Object> ghostFuture = FutureConverters.toJava(Patterns.ask(hamlet, "So art thou to revenge, when you shalt here", slowGhost)).toCompletableFuture();

        CompletableFuture<PoisonPill> claudiusFuture = CompletableFuture.allOf(hamletFuture, ghostFuture)
            .thenApply(v -> {
                hamletFuture.join();
                ghostFuture.join();
                return PoisonPill.getInstance();
            });

        ActorRef claudis = system.actorOf(Props.create(OutspokenActor.class), "Claudis");
        Patterns.pipe(claudiusFuture, system.dispatcher()).to(claudis);
        Thread.sleep(3000);
        System.out.println("KING CLAUDIS DIES: " + claudis.isTerminated());
    }
}
