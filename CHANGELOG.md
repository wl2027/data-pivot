<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# data-pivot-plugin Changelog

## [Unreleased]

## [2.2.0] - 2026-06-06
### Added
- 新增基于版本号的 GitHub Release 草稿流程，自动附加 `build/distributions` 中的插件 ZIP。
- 新增 GitHub Release 正式发布后自动推送 JetBrains Marketplace 的独立工作流。
- 恢复单元测试、集成测试和 UI 组件测试门禁，并提供手动跨平台 UI 组件测试。

### Changed
- 移除耗时且需要下载额外 IDE 的 Plugin Verifier 步骤。
- 测试任务禁用不兼容的 Gradle Configuration Cache，避免测试通过后因缓存序列化失败。

## [2.1.0] - 2026-06-05
### Changed
- 迁移到 IntelliJ Platform Gradle Plugin 2.x 最新模板结构，构建目标调整为 IntelliJ IDEA 2025.3+ / build 253+。
- 复用 IDEA Database Tools 数据源驱动，不再随插件打包 MySQL、PostgreSQL、Oracle、SQL Server、MongoDB 等数据库驱动。
- 明确数据库支持边界：当前维护 MySQL、PostgreSQL、Oracle、SQL Server 的导航和查询能力，不宣称覆盖 IDEA 支持的全部数据库方言。

### Added
- 增加单元测试、IntelliJ Platform 集成测试和设置页 UI 组件测试任务：`unitTest`、`integrationTest`、`ideaUiTest`。

## [2.0.0] - 2025-03-23
### Refactor
- 重构项目模块

## [1.1.2] - 2024-09-10

### Changed
- 关闭data-pivot mapping配置提醒

## [1.1.1] - 2024-09-07

### Changed
- 更新readme文件的数据库支持
- 关闭数据源检测提示


## [1.1.0] - 2024-07-18

### Added
- 便捷的装订线导航方式
- Data-Pivot Query 功能，支持便捷的数据查询
- Data-Pivot Query 功能支持远程搜索和本地搜索
- 提供默认映射策略，关联度匹配，匹配度为 0.9
- 提供关联度匹配映射的缓存功能
- 新增 data-pivot-test 模块
- 新增多数据源查询工具 QueryTool
- 优化 JDBC 连接池、防止 SQL 注入、支持中文搜索等

### Changed

- 更新操作说明 readme.md
- 更新 issue 模板
- 将 report 功能重命名为 Analysis
- Analysis 功能的映射配置采用关联度映射
- JDK 版本恢复为 JDK 11
- 更新包名



### Changed
- 关闭启动时数据连接检测
- 降低JDK版本
- 更改readme说明
- 先移除js解析引擎,插件瘦身

## [1.0.1] - 2024-07-04
### Changed
- 更新图标
- 更新插件ID
- 更新JDK版本

## [1.0.0] - 2024-02-24
### Added
- Project initialization, covering data analysis, ORM & ROM navigation.
- 项目初始化，包含数据分析、ORM&ROM导航。

[Unreleased]: https://github.com/wl2027/data-pivot-plugin/compare/2.2.0...HEAD
[2.2.0]: https://github.com/wl2027/data-pivot-plugin/compare/2.1.0...2.2.0
[2.1.0]: https://github.com/wl2027/runtime-pivot/compare/2.0.0...2.1.0
[2.0.0]: https://github.com/wl2027/runtime-pivot/compare/1.1.2...2.0.0
[1.1.2]: https://github.com/wl2027/runtime-pivot/compare/1.1.1...1.1.2
[1.1.1]: https://github.com/wl2027/runtime-pivot/compare/1.1.0...1.1.1
[1.1.0]: https://github.com/wl2027/runtime-pivot/compare/1.0.1...1.1.0
[1.0.1]: https://github.com/wl2027/runtime-pivot/compare/1.0.0...1.0.1
[1.0.0]: https://github.com/wl2027/runtime-pivot/commits/1.0.0
