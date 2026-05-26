# Carpet AS Addition

中文 | [English](README.en.md)

[Carpet mod](https://modrinth.com/mod/carpet) 的附属模组，为carpet提供额外可配置规则。在默认设置下**不会改变原版行为**。

## 简介

本模组面向使用 Carpet 假人 与相关功能的服务端/单机玩家，补充两类能力：

- **假人 UI 高亮**：在头顶名称标签、Tab 玩家列表、命令补全列表中，为假人名称行设置独立背景色，便于与真实玩家区分（需客户端同时安装本模组）。
- **假人睡眠忽略**：服务端在判断「是否可跳过夜晚」时忽略假人，仅统计真实玩家（纯服务端规则，无需客户端模组）。

规则通过 Carpet 标准机制注册，配置保存在世界的 `carpet.conf` 中，与 Carpet 本体共用同一文件。

## 支持版本

| Minecraft 版本 | Carpet 版本 | Java |
|----------------|------------|------|
| 1.20.1 | 1.4.112 | 17+ |
| 1.21.11 | 1.4.194 | 21+ |
| 26.1 | 26.1+v260401 | 25+ |

## 依赖

| 模组 | 说明 |
|------|------|
| [Fabric Loader](https://fabricmc.net/) | 0.19.2 及以上 |
| [Fabric API](https://modrinth.com/mod/fabric-api) | 与对应 MC 版本匹配的版本 |
| [Carpet](https://modrinth.com/mod/carpet) | 与对应 MC 版本匹配的版本 |

## 安装

1. 安装对应版本的 [Fabric Loader](https://fabricmc.net/)（需 0.19.2+）
2. 将 [Fabric API](https://modrinth.com/mod/fabric-api)、[Carpet](https://modrinth.com/mod/carpet) 与本模组 JAR 放入 `mods` 文件夹
3. 启动游戏

> JAR 文件名格式为 `carpet-as-addition-<模组版本>+<MC版本>.jar`，请按目标游戏版本选择对应构建。

## 使用方法

1. 进入世界后执行 `/carpet`
2. 在分类列表中选择 **[AS的附加包]**
3. 点击规则名即可切换开关或数值；需要永久保存时按提示确认

规则配置保存在世界的 **`carpet.conf`** 中，与 Carpet 本体共用同一文件。

## 规则一览

### 假人名称标签颜色

以下三个规则独立控制各 UI 位置的假人高亮，均默认关闭。**客户端需同时安装本模组才能看到颜色变化。**

可选颜色：`green`、`red`、`blue`、`yellow`、`orange`、`purple`、`white`、`aqua`；设为 `false` 表示关闭。

| 规则 | 默认 | 说明 |
|------|------|------|
| `fakePlayerNametagHead` | `false` | 假人头顶名称标签背景变为指定颜色 |
| `fakePlayerNametagTab` | `false` | Tab 玩家列表中假人所在行背景变为指定颜色 |
| `fakePlayerNametagCommand` | `false` | 命令补全列表中假人名称所在行背景变为指定颜色 |

### 假人睡眠忽略

| 规则 | 默认 | 说明 |
|------|------|------|
| `fakePlayerSleepIgnore` | 关闭 | 跳过夜晚时忽略所有假人，仅统计真实玩家；纯服务端规则，无需客户端安装本模组 |

## 问题反馈

请在 GitHub [Issues](https://github.com/AstraSolis/Carpet-AS-Addition/issues) 提交 Bug 报告或功能建议。反馈时请说明游戏版本、Carpet 版本及复现步骤。

## 许可证

本项目采用 [MIT](LICENSE) 许可证
