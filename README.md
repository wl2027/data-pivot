```banner
________      _____________________           __________._______   _______________________
\______ \    /  _  \__    ___/  _  \          \______   \   \   \ /   /\_____  \__    ___/
 |    |  \  /  /_\  \|    | /  /_\  \   ______ |     ___/   |\   Y   /  /   |   \|    |   
 |    `   \/    |    \    |/    |    \ /_____/ |    |   |   | \     /  /    |    \    |   
/_______  /\____|__  /____|\____|__  /         |____|   |___|  \___/   \_______  /____|   
        \/         \/              \/                                          \/         
```
# data-pivot-plugin

[![Version](https://img.shields.io/jetbrains/plugin/v/com.github.wl2027.datapivotplugin.svg)](https://plugins.jetbrains.com/plugin/23828-data-pivot)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.github.wl2027.datapivotplugin.svg)](https://plugins.jetbrains.com/plugin/23828-data-pivot)
![Downloads](https://img.shields.io/github/release/wl2027/data-pivot.svg)
![Downloads](https://img.shields.io/github/stars/wl2027/data-pivot)
![Downloads](https://img.shields.io/badge/license-GPLv3-blue.svg)
![Downloads](https://img.shields.io/badge/MySQL-5.7%2B-brightgreen.svg?style=flat)
![Downloads](https://img.shields.io/badge/Java-11-brightgreen.svg?style=flat)
[![GitHub](https://img.shields.io/static/v1?label=&message=GitHub&logo=github&color=black&labelColor=555)](https://github.com/wl2027/data-pivot) 
[![Gitee](https://img.shields.io/static/v1?label=&message=Gitee&logo=gitee&color=orange&labelColor=555)](https://gitee.com/wl2027/data-pivot)

## Introduction
<!-- Plugin description -->
### English:
**data-pivot** is a data analysis tool for IDEA, designed to provide developers with convenient object-relational mapping navigation and data querying and reporting analysis functions.

**Tool Features**

- **Default Association Strategy**: ORM and ROM mapping strategies based on association.
- **Precise Configurable Mapping**: Flexible and configurable ORM and ROM mapping strategies.
- **Convenient Data Matching Query**: Fast and efficient object data querying function.
- **Convenient Data Analysis Reports**: Easily generate analysis reports for object data.

**Usage Advantages**

In complex database scenarios, such as dealing with numerous database tables, multiple fields, or special naming rules, data-pivot can significantly reduce repetitive and mechanical manual operations, greatly improving development efficiency.

**Database Support**

Currently, data-pivot fully supports MySQL databases and is also compatible with Oracle, MongoDB, SQL Server, PostgreSQL, and other popular databases by default [Database Ranking: https://db-engines.com/en/ranking](https://db-engines.com/en/ranking).

**Future Plans**

In the future, we will continue to expand and optimize the plugin's functions, providing more configurable strategy mappings and configurable report scripts, while also adapting to more databases.

Detailed operation documents: [https://github.com/wl2027/data-pivot](https://github.com/wl2027/data-pivot)

### 中文:
**data-pivot** 是一个IDEA的数据分析工具，旨在为开发人员提供便捷的对象关系映射导航和数据查询和报表分析功能。

**工具特性**

- **默认关联策略**：基于关联度的 ORM 和 ROM 映射策略。
- **精准配置映射**：灵活可配置的 ORM 和 ROM 映射策略。
- **便捷的数据匹配查询**：快速高效的对象数据查询功能。
- **便捷的数据分析报表**：轻松生成对象数据的分析报表。

**使用优势**

在复杂数据库场景下，例如面对大量数据库表、众多字段或特殊命名规则等情况，data-pivot 可以显著减少重复性和机械性的人工操作，大幅提升开发效率。

**数据库支持**

目前，data-pivot 已全面支持 MySQL 数据库，并默认兼容 Oracle、MongoDB、SQL Server、PostgreSQL 等主流数据库。

[数据库热度排行:https://db-engines.com/en/ranking](https://db-engines.com/en/ranking)。

**未来规划**

未来，我们将继续扩展和优化插件功能，提供更多配置化策略映射和配置化报表脚本，同时适配更多数据库。


详细操作说明文档: [https://github.com/wl2027/data-pivot](https://github.com/wl2027/data-pivot)

<!-- Plugin description end -->

## Features
- **Data-Pivot Query**: Provides convenient field data querying capabilities, quickly filtering required data through remote and local searches, and supports one-click copying.提供便捷的字段数据查询功能，通过远程搜索和本地搜索快速筛选所需数据，并支持一键复制。
- **Data-Pivot Analysis**: Offers real-time field data report analysis, examining field data conditions such as data values and their distribution.提供实时字段数据报表分析功能，分析字段数据情况，如数据值及其数量占比等。
- **Data-Pivot Configuration**: Supports various mapping strategies, including MyBatis-Plug annotations, JPA annotations, and camel case to underscore naming rules.支持多种映射策略，包括 MyBatis-Plug 注解、JPA 注解以及驼峰与下划线命名规则。
- **Data-Pivot ORM/ROM**: Provides precise and intelligent association jumps between object attributes and database fields, efficiently finding the required fields in complex database scenarios, simplifying the field search process.提供对象属性与数据库字段之间精准、智能的关联跳转功能，在复杂数据库场景中高效查找所需字段，简化字段查找过程。

## Installation

- **Using the IDE built-in plugin system:**

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "data-pivot"</kbd> >
  <kbd>Install</kbd>

- **Manually:**

  Download the [latest release](https://github.com/wl2027/data-pivot-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

Restart the **IDE** after installation.

## Using The Plugin

### 1. Navigate via Row Mapping Based on Associations. 根据关联度映射的行标进行导航跳转

![1.navigation.gif](doc%2Fimage%2F1.navigation.gif)

### 2. Perform Data Queries via Association Mapping. 根据关联度映射的查询进行数据查询

- **Remote Search**: Fuzzy matching for selected conditional fields. 远程搜索:模糊匹配选中的条件字段

![2.1_remote_query.gif](doc%2Fimage%2F2.1_remote_query.gif)

- **Local Search**: Multi-item matching within the current table content. 本地搜索:当前表格内容的多项匹配匹配

![2.2_local_query.gif](doc%2Fimage%2F2.2_local_query.gif)

- **Select Fields for Query**: Query all fields with a single field selected, query specified fields with multiple texts selected, using the last selected field as the condition field. 选择单字段查询全部,选择多文本查询指定字段,选择的最后一个字段作为条件字段

![2.3_query_select_column.gif](doc%2Fimage%2F2.3_query_select_column.gif)

- **Copy Selected Table Row Data** 复制选中的表格行数据

![2.4_copy_row_josn.gif](doc%2Fimage%2F2.4_copy_row_josn.gif)


### 3 Perform Data Analysis on Class Fields 类字段进行数据分析

![3.data_pivot_analysis.gif](doc%2Fimage%2F3.data_pivot_analysis.gif)

### 4 Set Project Mapping Strategy 设置项目映射策略

![4.data_pivot_setting.gif](doc%2Fimage%2F4.data_pivot_setting.gif)


### 5 Navigate and Jump between Class Fields 类字段进行导航跳转

![5.data_pivot_orm.gif](doc%2Fimage%2F5.data_pivot_orm.gif)

### 6 Navigate and Jump between Database columns 数据库字段进行导航跳转

![6.data_pivot_rom.gif](doc%2Fimage%2F6.data_pivot_rom.gif)

### 7 Navigation and jumping of fields in the database console 在数据库控制台进行字段进行导航跳转

![7.data_pivot_rom_console.gif](doc%2Fimage%2F7.data_pivot_rom_console.gif)

## Compatibility

- [ ] Android Studio
- [ ] AppCode
- [ ] CLion
- [ ] DataGrip
- [ ] GoLand
- [ ] HUAWEI DevEco Studio
- [x] **IntelliJ IDEA Ultimate**
- [ ] IntelliJ IDEA Community
- [ ] IntelliJ IDEA Educational
- [ ] MPS
- [ ] PhpStorm
- [ ] PyCharm Professional
- [ ] PyCharm Community
- [ ] PyCharm Educational
- [ ] Rider
- [ ] RubyMine
- [ ] WebStorm


## Contributing

Welcome to contribute to the project! You can fix bugs by submitting a Pull Request (PR) or discuss new features or changes by creating an [Issue](https://github.com/wl2027/data-pivot-plugin/issues/). Look forward to your valuable contributions!

欢迎参与项目贡献！如您可以通过提交Pull Request（PR）来修复bug，或者新建 [Issue](https://github.com/wl2027/data-pivot-plugin/issues/) 来讨论新特性或变更，期待您的宝贵贡献！

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
