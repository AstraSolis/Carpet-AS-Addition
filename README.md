# Carpet AS Addition

[Carpet mod](https://modrinth.com/mod/carpet) 的附属模组，为carpet提供额外可配置规则。在默认设置下**不会改变原版行为**。

## 依赖

| 模组 | 说明 |
|------|------|
| [Fabric Loader](https://fabricmc.net/) | 0.19.2 及以上 |
| [Fabric API](https://modrinth.com/mod/fabric-api) | 与 1.21.11 匹配的版本 |
| [Carpet](https://modrinth.com/mod/carpet) | 1.4.194（1.21.11）或兼容版本 |

## 安装

1. 安装 Fabric 
2. 将 **Fabric API**、**Carpet** 与本模组 JAR 放入 `mods` 文件夹
3. 启动游戏

## 使用方法

1. 进入世界后执行 `/carpet`
2. 在分类列表中选择 **[AS的附加包]**
3. 点击规则名即可切换开关或数值；需要永久保存时按提示确认

规则配置保存在世界的 **`carpet.conf`** 中，与 Carpet 本体共用同一文件。

## 规则一览

| 规则 | 默认 | 说明 |
|------|------|------|
| `exampleRule` | 关闭 | 开启后，在服务器加载完成时向日志输出一条提示（用于验证模组加载） |

## 问题反馈

请在 GitHub [Issues](https://github.com/AstraSolis/Carpet-AS-Addition/issues) 提交 Bug 报告或功能建议。反馈时请说明游戏版本、Carpet 版本及复现步骤。

## 许可证

本项目采用 [MIT](LICENSE) 许可证
