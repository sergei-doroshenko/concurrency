package org.sdoroshenko.akkatypedjava.psassembly;


import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import org.sdoroshenko.akkatypedjava.psassembly.actors.AssemblyActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AssemblyService {

    private final ActorSystem<AssemblyActor.AssembleResponse> system = ActorSystem.create(Behaviors.empty(), "AssemblyService");
    private final Scheduler scheduler = system.scheduler();
    private final Duration timeout = Duration.ofMillis(100);

    public void shutdown() {
        system.terminate();
    }

    private String generateName(String prefix) {
        return prefix + System.nanoTime() + "end";
    }
    ActorRef<AssemblyActor.AssembleRequest> actor0 = system.systemActorOf(AssemblyActor.create(), generateName("CpuInstallAssembleActor"), Props.empty());
    ActorRef<AssemblyActor.AssembleRequest> actor1 = system.systemActorOf(AssemblyActor.create(), generateName("MemoryInstallAssembleActor"), Props.empty());
    ActorRef<AssemblyActor.AssembleRequest> actor2 = system.systemActorOf(AssemblyActor.create(), generateName("SSDInstallAssembleActor"), Props.empty());
    ActorRef<AssemblyActor.AssembleRequest> actor3 = system.systemActorOf(AssemblyActor.create(), generateName("EnclosureAssembleActor"), Props.empty());


    public CompletionStage<PC> installCPU(PC target) {
        System.out.println("Service trigger installing cpu in: " + target.serialNumber + ", thread: " + Thread.currentThread().getName());


        CompletionStage<AssemblyActor.AssembleResponse> result = AskPattern.ask(
                actor0,
                replyTo -> new AssemblyActor.CpuInstallAssembleRequest(target, replyTo),
                timeout,
                scheduler);

        return result.thenApply(AssemblyActor.AssembleResponse::getResult);

    }

    public CompletionStage<PC> installMemory(PC target) {
        System.out.println("Service trigger installing memory in: " + target + ", thread: " + Thread.currentThread().getName());

        CompletionStage<AssemblyActor.AssembleResponse> result = AskPattern.ask(
                actor1,
                replyTo -> new AssemblyActor.MemoryInstallAssembleRequest(target, replyTo),
                timeout,
                scheduler);

        return result.thenApply(AssemblyActor.AssembleResponse::getResult);

    }

    public CompletionStage<PC> installSSD(PC target) {
        System.out.println("Service trigger installing ssd in: " + target + ", thread: " + Thread.currentThread().getName());

        CompletionStage<AssemblyActor.AssembleResponse> result = AskPattern.ask(
                actor2,
                replyTo -> new AssemblyActor.SSDInstallAssembleRequest(target, replyTo),
                timeout,
                scheduler);

        return result.thenApply(AssemblyActor.AssembleResponse::getResult);

    }

    public CompletionStage<PC> installEnclosure(PC target) {
        System.out.println("Service trigger installing enclosure in: " + target + ", thread: " + Thread.currentThread().getName());

        CompletionStage<AssemblyActor.AssembleResponse> result = AskPattern.ask(
                actor3,
                replyTo -> new AssemblyActor.EnclosureInstallAssembleRequest(target, replyTo),
                timeout,
                scheduler);

        return result.thenApply(AssemblyActor.AssembleResponse::getResult);

    }
}
