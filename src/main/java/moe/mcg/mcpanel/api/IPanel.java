package moe.mcg.mcpanel.api;

/**
 * 面板接口，用于刷新面板数据。
 * <p>
 * 该接口定义了一个通用的方法 {@link #refresh(Object)}，用于刷新面板的数据，
 * 通过传递指定类型的数据对象 {@code T}，可以根据需要更新面板的显示内容。
 * </p>
 *
 * @param <T> 数据类型，表示刷新时传递的数据类型
 */
public interface IPanel<T> {
    /**
     * 刷新面板数据。
     * <p>
     * 实现此方法时，应该使用传递的 {@code data} 对象来更新面板的显示内容。
     * </p>
     *
     * @param data 刷新面板时所需的数据
     */
    void refresh(T data);
}