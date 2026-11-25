# udf_offline
UDF（User Defined Function，用户自定义函数）是你用 Java/Scala 写好、打包成 jar，注册到 Hive 后就能像 `concat`、`get_json_object` 一样在 SQL 里调用的函数。当内置函数满足不了需求（比如解析特定结构 JSON、自定义脱敏、业务规则计算）时，就自己写一个。