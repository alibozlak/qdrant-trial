package dev.bozlak.qdrant_trial;

@FunctionalInterface 
public interface ConsumerWithException<T> {

    void accept(T t) throws Exception;
}
