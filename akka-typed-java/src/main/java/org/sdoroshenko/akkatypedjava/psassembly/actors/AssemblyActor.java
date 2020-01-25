package org.sdoroshenko.akkatypedjava.psassembly.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.japi.function.Function;
import org.sdoroshenko.akkatypedjava.psassembly.PC;

public class AssemblyActor extends AbstractBehavior<AssemblyActor.AssembleRequest> {

    public interface AssembleRequest {}

    public static class DefaultAssembleRequest implements AssembleRequest {
        public final PC target;
        public final ActorRef<AssembleResponse> replyTo;

        public DefaultAssembleRequest(PC target, ActorRef<AssembleResponse> replyTo) {
            this.target = target;
            this.replyTo = replyTo;
        }
    }

    public static class CpuInstallAssembleRequest extends DefaultAssembleRequest {
        public CpuInstallAssembleRequest(PC target, ActorRef<AssembleResponse> replyTo) {
            super(target, replyTo);
        }
    }

    public static class MemoryInstallAssembleRequest extends DefaultAssembleRequest {
        public MemoryInstallAssembleRequest(PC target, ActorRef<AssembleResponse> replyTo) {
            super(target, replyTo);
        }
    }

    public static class SSDInstallAssembleRequest extends DefaultAssembleRequest {
        public SSDInstallAssembleRequest(PC target, ActorRef<AssembleResponse> replyTo) {
            super(target, replyTo);
        }
    }

    public static class EnclosureInstallAssembleRequest extends DefaultAssembleRequest {
        public EnclosureInstallAssembleRequest(PC target, ActorRef<AssembleResponse> replyTo) {
            super(target, replyTo);
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
                .onMessage(CpuInstallAssembleRequest.class, cpuInstallFunction)
                .onMessage(MemoryInstallAssembleRequest.class, memoryInstallFunction)
                .onMessage(SSDInstallAssembleRequest.class, ssdInstallFunction)
                .onMessage(EnclosureInstallAssembleRequest.class, enclosureInstallFunction)
                .onAnyMessage(o -> Behaviors.same())
                .build();
    }

    private Function<CpuInstallAssembleRequest, Behavior<AssembleRequest>> cpuInstallFunction = request -> {
        PC pc = request.target;
        System.out.println("Actor installing cpu in: " + pc + ", thread: " + Thread.currentThread().getName());
        pc.components.add("cpu");
        request.replyTo.tell(new DefaultAssembleResponse(pc));
        return Behaviors.same();
    };

    private Function<MemoryInstallAssembleRequest, Behavior<AssembleRequest>> memoryInstallFunction = request -> {
        PC pc = request.target;
        System.out.println("Actor installing memory in: " + pc + ", thread: " + Thread.currentThread().getName());
        pc.components.add("memory");
        request.replyTo.tell(new DefaultAssembleResponse(pc));
        return Behaviors.same();
    };

    private Function<SSDInstallAssembleRequest, Behavior<AssembleRequest>> ssdInstallFunction = request -> {
        PC pc = request.target;
        System.out.println("Actor installing ssd in: " + pc + ", thread: " + Thread.currentThread().getName());
        pc.components.add("ssd");
        request.replyTo.tell(new DefaultAssembleResponse(pc));
        return Behaviors.same();
    };

    private Function<EnclosureInstallAssembleRequest, Behavior<AssembleRequest>> enclosureInstallFunction = request -> {
        PC pc = request.target;
        System.out.println("Actor installing enclosure in: " + pc + ", thread: " + Thread.currentThread().getName());
        pc.components.add("enclosure");
        request.replyTo.tell(new DefaultAssembleResponse(pc));
        return Behaviors.same();
    };
}
