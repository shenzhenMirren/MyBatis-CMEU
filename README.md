## 基本介绍
官方QQ交流群号:519714660;<br/>
Mybatis-CMEU全称为：Mybatis Config Mapper Util ;<br/>
是基于javafx8开发的一款图形界面的Mybatis逆向工程;<br/>
该工具支持Oracle , SqlServer , MySQL , PostgreSql数据库的逆向生成;<br/>
## 基本常用功能：<br/>
<ol>
<li>生成实体类(可以自定义：gett,有参无参构造方法,自定义类型与属性,序列化等);</li>
<li>生成dao层接口(查询全部信息，通过ID查询信息,插入全部属性,插入不为空的属性,通过ID更新全部属性,通过ID更新不为空的属性,通过Assist更新全部属性,通过Assist更新不为空的属性,通过ID删除信息,通过Assist删除信息);</li>
<li>生成Mapper映射文件(dao层接口SQL语句,支持生成3表关联(比如：A表关联B表,B表关联C表,一次便可获得3张表的数据),支持主键策略;&lt;如果选择创建Assist支持分页,去重,排序,无注入动态查询等&gt;);</li>
<li>生成service层接口(与dao接口一致)可选项;</li>
<li>生成service层实现类(实现service层接口)可选项;</li>
<li>生成查询工具Assist(Assist为CMEU特别定制的查询工具,使用该工具一切操作都变得超简单,比如：分页通过Assist只需要设置2个参数就可以实现比如参数1=10,参数2=5,查询出来就是第10行到15行的数据,同时也可以防注入动态添加查询添加,去重,排序,自定义返回列等)可选项;</li>
<li>生成mybatis配置文件(mybatis的主配置文件,系统会自动识别使用的数据库，并创建其连接,同时更新mapper映射文件的资源路径)可选项;</li>
<li>生成mybatisUtil(用于获得SqlSession等操作,当与mybatis配置文件一同创建时系统会自动识别配置文件路径并设置)可选项;</li>
<li>更新现有配置文件的mapper映射文件的资源路径(当生成新的信息时自动更新mybatis配置文件的Mapper映射资源路径)可选项;</li>
</ol>
[MyBatis-CMEU使用帮助文档](https://github.com/shenzhenMirren/MyBatis-CMEU-DOC)<br/>
本工具基于JKD1.8_U66开发运行环境需要JKD1.8以上,在MyEclipse中如果已经安装了javafx的运行环境可以直接在MyEclipse中运行,否则需要到QQ交流群里下载可执行文件;<br/>
## 软件主页:<br/>
![index](https://github.com/shenzhenMirren/MyBatis-CMEU-DOC/master/resource/images/1.png)
  


