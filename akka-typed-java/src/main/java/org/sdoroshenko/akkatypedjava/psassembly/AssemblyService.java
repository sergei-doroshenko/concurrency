package org.sdoroshenko.akkatypedjava.psassembly;


import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import org.sdoroshenko.akkatypedjava.psassembly.actors.AssemblyActor;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AssemblyService {

    private ActorSystem<AssemblyActor.AssembleResponse> system = ActorSystem.create(Behaviors.empty(), "AssemblyService");

    public void shutdown() {
        system.terminate();
    }

    public CompletionStage<PC> installCPU(PC target) {
        ActorRef<AssemblyActor.AssembleRequest> actor = system.systemActorOf(AssemblyActor.create(), "AssembleActor", Props.empty());

        CompletionStage<AssemblyActor.AssembleResponse> result = AskPattern.ask(
            actor,
            replyTo -> new AssemblyActor.CpuInstallAssembleRequest(target, replyTo),
            Duration.ofSeconds(10),
            system.scheduler());

        return result.thenApply(AssemblyActor.AssembleResponse::getResult);

    }
}
