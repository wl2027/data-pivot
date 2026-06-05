
- 面向“业务代码字段”的一键定位：从 Java 字段直接跳到数据库列，尤其是 MyBatis-Plus、旧项目命名约定、模块/包/库绑定这种非标准场景。
- 反向定位：从数据库列、查询结果上下文回到 Java 字段。IDEA 的 Persistence 更偏 JPA/Spring Data 模型，不一定覆盖 MyBatis-Plus 和自定义命名体系。
- 上下文查询：用户选中 Java 字段后自动推断表/列并查样本数据。IDEA 有 Query Console，但不会天然知道“这个 Java 字段对应哪张表哪一列”。
- 字段分布分析：从代码字段直接看值分布/占比，这是代码语境下的数据洞察，而不是普通表格浏览。

**更新路线**
建议路线：

1. **定位重写**
   产品定位改为：`IDEA Database Tools companion for Java ORM/Data mapping navigation, contextual query, and field analysis`。中文就是“IDEA 数据库工具的 Java 字段映射增强插件”。

2. **短期，2.x 稳定化**
   只支持 IntelliJ IDEA 2025.3+ unified/free 层能跑的能力；核心依赖 `com.intellij.database`，不打包驱动。文档明确：不是支持 IDEA 全部数据库查询，只是复用 IDEA 数据源，当前查询方言维护 MySQL/PostgreSQL/Oracle/SQL Server。

3. **中期，强化映射核心**
   把映射抽象成 `MappingProvider`：JPA、MyBatis-Plus、驼峰/下划线、自定义规则。下一步优先补 MyBatis XML、Spring Data JDBC、jOOQ、Kotlin data class/Java record、Lombok 字段识别。

4. **中期，减少重复 UI**
   查询结果表格只做“代码字段上下文样本/分析”，复杂编辑、导出、全库搜索交给 IDEA。能打开 IDEA Query Console 的地方优先打开/注入 SQL，而不是自己变成 DataGrip 子集。

5. **长期，做 IDEA 没有的高级价值**
   做“代码模型和数据库模型漂移检测”：字段缺失、类型不一致、命名不一致、表字段无人使用、Java 字段无数据库列。这个方向比再做一个查询面板更有护城河。

6. **质量路线**
   建一个 sample project + sample datasource 测试矩阵：JPA、MyBatis-Plus、无注解命名策略各一套；MySQL/PostgreSQL/Oracle/SQL Server 至少覆盖 SQL 生成单测，集成测试覆盖 Database PSI 映射，UI 测试覆盖 action 可见性和跳转入口。
