package moe.mcg.mcpanel.api;

/**
 * 资源接口，用于获取指定路径的资源。
 * <p>
 * 该接口定义了一个方法 {@link #getResource(String)}，用于通过给定的路径获取资源，
 * 返回的资源类型由实现类指定的泛型 {@code T} 确定。
 * </p>
 *
 * @param <T> 资源类型，表示返回的资源对象类型
 */
public interface IResource<T> {
    T getResource(String path);
}
