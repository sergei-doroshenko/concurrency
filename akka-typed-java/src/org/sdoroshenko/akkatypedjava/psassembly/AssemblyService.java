package org.sdoroshenko.akkatypedjava.psassembly;


import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AssemblyService {

    private ActorSystem<PC> system = ActorSystem.create(AssemblyActor.create(), "ROOT");

    interface Command {}
    public static class GiveMePC implements Command {
        public final int count;
        public final ActorRef<Reply> replyTo;

        public GiveMePC(int count, ActorRef<Reply> replyTo) {
            this.count = count;
            this.replyTo = replyTo;
        }
    }

    interface Reply {}

    public static class Cookies implements Reply {
        public final int count;

        public Cookies(int count) {
            this.count = count;
        }
    }


    public CompletionStage<PC> installCPU(ActorSystem<Void> system, ActorRef<Command> cpuInstaller) {
        /*result = AskPattern.ask(
                cpuInstaller,
                replyTo -> new GiveMePC(3, replyTo),
                Duration.ofSeconds(3),
                system.scheduler());*/
        return null;

    }
}
