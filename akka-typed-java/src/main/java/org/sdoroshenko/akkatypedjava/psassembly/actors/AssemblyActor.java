package org.sdoroshenko.akkatypedjava.psassembly.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.sdoroshenko.akkatypedjava.psassembly.PC;

public class AssemblyActor extends AbstractBehavior<AssemblyActor.AssembleRequest> {

    public interface AssembleRequest {}

    public static class CpuInstallAssembleRequest implements AssembleRequest {
        public final PC target;
        public final ActorRef<AssembleResponse> replyTo;

        public CpuInstallAssembleRequest(PC target, ActorRef<AssembleResponse> replyTo) {
            this.target = target;
            this.replyTo = replyTo;
        }
    }

    public interface AssembleResponse {
        PC getResult();
    }

    public static class DefaultAssembleResponse implements AssembleResponse {

        private final PC result;

        public DefaultAssembleResponse(PC result) {
            this.result = result;
        }

        @Override
        public PC getResult() {
            return result;
        }
    }

    public AssemblyActor(ActorContext<AssembleRequest> context) {
        super(context);
    }

    public static Behavior<AssembleRequest> create() {
        return Behaviors.setup(ctx -> new AssemblyActor(ctx));
    }

    @Override
    public Receive<AssembleRequest> createReceive() {
        return newReceiveBuilder()
                .onMessage(CpuInstallAssembleRequest.class, request -> {
                    PC pc = request.target.copy();
                    pc.cpu = true;
                    System.out.println(request.replyTo);
                    request.replyTo.tell(new DefaultAssembleResponse(pc));
                    return Behaviors.same();
                })
                .onAnyMessage(o -> Behaviors.same())
                .build();
    }
}
