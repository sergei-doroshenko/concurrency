package org.sdoroshenko.pcassembly;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.compat.java8.FutureConverters;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class AssemblyService {
    private static final Timeout  assembleTimeout = new Timeout(Duration.create(2, TimeUnit.SECONDS));

    private ActorSystem system = ActorSystem.create("ROOT");

    public CompletionStage<Object> installCPU(PC pc) {
        ActorRef cpuInstaller = system.actorOf(Props.create(AssemblyActor.class), "CPUInstaller");
        return FutureConverters.toJava(Patterns.ask(cpuInstaller, Command.InstallCPU, assembleTimeout))
                .toCompletableFuture();
    }
}
