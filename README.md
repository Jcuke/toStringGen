generate a toString() with a json format

select single or multiple java class file, click toString via rightKey,


右键选择一个或多个java类文件，批量生成 toString(), 此方法相当于将对象转换成json字符串, 不同与idea自带toString()方法。
特别是用于在日志中打印对象时，直接 logging.info(对象引用变量), 即可以打印对象的所有属性的值，并以json格式展示，且支持复杂对象,
包含自定义类类型，集合，数组，键值对等. 如果toString()方法已存在，则直接覆盖

by qq963577663 wangjinwen 2018-06-17