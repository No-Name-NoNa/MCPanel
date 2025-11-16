# MCPanel - 构建与启动指南

## 构建可执行文件

要构建 MCPanel 项目的可执行文件，请按照以下步骤进行：

1. 打开 IntelliJ IDEA，并确保已正确导入 **MCPanel** 项目。
2. 确保项目的 Java 版本设置为 **Java 21**，并且 Gradle 已成功加载项目。
3. 在 IntelliJ IDEA 中，打开 **Gradle** 工具窗口。
4. 在 **Gradle** 面板中，依次展开：
    - `Tasks`
    - `shadow`
5. 然后点击 **shadowJar** 任务，开始构建可执行的 `.jar` 文件。

构建完成后，可执行的 `.jar` 文件将会保存在项目的 `build/libs/` 目录下。

## 启动 MCPanel

1. 在 IntelliJ IDEA 中，选择 **MCPanel** 项目。
2. 点击 **application-run** 运行配置，启动 **MCPanel** 客户端。

此时，MCPanel 客户端将启动并准备好与服务器进行交互。

## 说明

- **shadowJar** 任务将项目打包成一个独立的 `.jar` 文件，包含所有的依赖。
- **application-run** 配置用于启动 MCPanel 客户端，使其能够与服务器连接并进行交互。