package com.wiredi.kafka.consumer.container;

import com.wiredi.runtime.async.barriers.MutableBarrier;

import java.util.List;

public enum ContainerState {
    CLOSED {
        @Override
        public void apply(MutableBarrier startingBarrier, MutableBarrier shutdownBarrier) {
            startingBarrier.close();
            shutdownBarrier.open();
        }

        @Override
        public void validateStep(ContainerState previous) {
            requirePreviousState(List.of(ContainerState.SHUTTING_DOWN), previous);
        }
    },
    STARTING {
        @Override
        public void apply(MutableBarrier startingBarrier, MutableBarrier shutdownBarrier) {
            startingBarrier.close();
            shutdownBarrier.close();
        }

        @Override
        public void validateStep(ContainerState previous) {
            if (previous != ContainerState.STARTING) {
                requirePreviousState(List.of(ContainerState.CLOSED), previous);
            }
        }
    },
    RUNNING {
        @Override
        public void apply(MutableBarrier startingBarrier, MutableBarrier shutdownBarrier) {
            startingBarrier.open();
            shutdownBarrier.close();
        }

        @Override
        public void validateStep(ContainerState previous) {
            requirePreviousState(List.of(ContainerState.STARTING), previous);

        }
    },
    SHUTTING_DOWN {
        @Override
        public void apply(MutableBarrier startingBarrier, MutableBarrier shutdownBarrier) {
            startingBarrier.close();
            shutdownBarrier.close();
        }

        @Override
        public void validateStep(ContainerState previous) {
            requirePreviousState(List.of(ContainerState.SHUTTING_DOWN, ContainerState.STARTING, ContainerState.RUNNING), previous);
        }
    };

    public abstract void apply(
            MutableBarrier startingBarrier,
            MutableBarrier shutdownBarrier
    );

    public abstract void validateStep(ContainerState previous);

    protected void requirePreviousState(List<ContainerState> expected, ContainerState actual) {
        if (!expected.contains(actual)) {
            throw new IllegalStateTransitionException(this, actual);
        }
    }

    public static class IllegalStateTransitionException extends RuntimeException {
        private final ContainerState currentState;
        private final ContainerState previousState;

        public IllegalStateTransitionException(ContainerState currentState, ContainerState previousState) {
            super("Unable to transition from " + previousState + " to " + currentState);
            this.currentState = currentState;
            this.previousState = previousState;
        }

        public ContainerState currentState() {
            return currentState;
        }

        public ContainerState previousState() {
            return previousState;
        }
    }
}