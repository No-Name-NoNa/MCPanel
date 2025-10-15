package moe.mcg.mcpanel.api;

public interface IResource<T> {
    T getResource(String path);
}
