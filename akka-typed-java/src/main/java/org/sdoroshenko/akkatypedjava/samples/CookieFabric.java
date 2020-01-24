package org.sdoroshenko.akkatypedjava.samples;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.*;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class CookieFabric extends AbstractBehavior<CookieFabric.Command> {
    interface Command {
    }

    public static class GiveMeCookies implements Command {
        public final int count;
        public final ActorRef<Reply> replyTo;

        public GiveMeCookies(int count, ActorRef<Reply> replyTo) {
            this.count = count;
            System.out.println(replyTo);
            this.replyTo = replyTo;
        }
    }

    interface Reply {
    }

    public static class Cookies implements Reply {
        public final int count;

        public Cookies(int count) {
            this.count = count;
        }
    }

    public static class InvalidRequest implements Reply {
        public final String reason;

        public InvalidRequest(String reason) {
            this.reason = reason;
        }
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(CookieFabric::new);
    }

    private CookieFabric(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder().onMessage(GiveMeCookies.class, this::onGiveMeCookies).build();
    }

    private Behavior<Command> onGiveMeCookies(GiveMeCookies request) {
        if (request.count >= 5) request.replyTo.tell(new InvalidRequest("Too many cookies."));
        else request.replyTo.tell(new Cookies(request.count));

        return this;
    }

    public static void askAndPrint(ActorSystem<Void> system, ActorRef<CookieFabric.Command> cookieFabric) {
        System.out.println(cookieFabric);
        CompletionStage<Reply> result = AskPattern.ask(
                cookieFabric,
                replyTo -> new CookieFabric.GiveMeCookies(3, replyTo),
                // asking someone requires a timeout and a scheduler, if the timeout hits without
                // response the ask is failed with a TimeoutException
                Duration.ofSeconds(3),
                system.scheduler()
        );

        result.whenComplete(
                (reply, failure) -> {
                    if (reply instanceof CookieFabric.Cookies)
                        System.out.println("Yay, " + ((CookieFabric.Cookies) reply).count + " cookies!");
                    else if (reply instanceof CookieFabric.InvalidRequest)
                        System.out.println("No cookies for me. " + ((CookieFabric.InvalidRequest) reply).reason);
                    else System.out.println("Boo! didn't get cookies in time. " + failure);
                });
    }

    public static void main(String[] args) {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "root");
        ActorRef<CookieFabric.Command> cookieFabric = system.systemActorOf(create(), "factory", Props.empty());
        askAndPrint(system, cookieFabric);
        system.terminate();
    }
}
