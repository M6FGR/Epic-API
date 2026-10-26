package dev.m6fgr.epic_api.network.interfaces;

@FunctionalInterface
public interface StreamMemberDecoder<O, T> {
    void decode(T value, O output);
}