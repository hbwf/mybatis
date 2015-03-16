This page provides some information that is useful when migrating a project from iBatis 2 to MyBatis 3.  It is probably not 100% complete.

The project is currently at Github https://github.com/mybatis/ibatis2mybatis

# Conversion Tool #

There is a tool available in the downloads section that will help you to convert your iBATIS 2.x sqlmap files into MyBatis 3.x xml mapper files.

Get it from http://mybatis.googlecode.com/files/ibatis2mybatis.zip

The tool is designed around an xslt transformation and some text replacements packaged in an ant task and tries to deliver a good starting point before the more complex work begins.

# New DTDs #

New sqlMapConfig.xml DTD:

```
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
```

New sqlMap (`*`.map.xml) DTD:

```
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd"> 
```

# Configuration #

  * Root configuration tag `<sqlMapConfig>` is now `<configuration>`

## Settings ##

Within the root configuration tag:

```
<settings x="y" foo="bar"/>
```

is now:

```
<settings>
    <setting name="x" value="y"/>
    <setting name="foo" value="bar"/>
</settings>
```

and

```
<settings useStatementNamespaces="true"/>
```

can be removed, since the use of namespaces has become mandatory.

## `<typeAlias>` ##

`<typeAlias>` must be moved out of the `<sqlMap>` element to `<configuration><typeAliases></typeAliases></configuration>`

```
<configuration>
    <settings>
    ...
    </settings>
    <typeAliases>
        <typeAlias ... />
    </typeAliases>
</configuration>
```

## `<transactionManager>` and `<dataSource>` ##

```
<transactionManager type="JDBC" commitRequired="false">
    <dataSource type="your.package.CustomDataSourceFactory" />
</transactionManager>
```

is now:

```
<environments default="env">
    <environment id="env">
        <transactionManager type="JDBC">
            <property name="commitRequired" value="false"/>
        </transactionManager>
        <dataSource type="your.package.CustomDataSourceFactory" />
    </environment>
</environments>
```

## `<sqlMap>` ##

```
<sqlMap resource=... />
<sqlMap resource=... />
<sqlMap resource=... />
```

is now:

```
<mappers>
    <mapper resource=... />
</mappers>
```


# Mapping #

  * The root element `<sqlMap>` is now `<mapper>`
  * The attribute `parameterClass` should be changed to `parameterType`
  * The attribute `resultClass` should be changed to `resultType`
  * The attribute `class` should be changed to `type`
  * the `columnIndex` attribute does not exist anymore for the `<result>` tag
  * The `groupBy` attribute has been eliminated.  Here is an example of `groupBy` from a 2.x sqlMap:

```
<resultMap id="productRM" class="product" groupBy="id">
    <result property="id" column="product_id"/>
    <result property="name" column="product_name"/>
    <result property="category" column="product_category"/>
    <result property="subProducts" resultMap="Products.subProductsRM"/>
</resultMap>
```

New:

```
<resultMap id="productRM" type="product" >
    <id property="id" column="product "/>
    <result property="name " column="product_name "/>
    <result property="category " column="product_category "/>
    <collection property="subProducts" resultMap="Products.subProductsRM"/>
</resultMap>
```

## Nested resultMaps ##
These should now be specified using the `<association>` tag.

```
<resultMap ...>
    <result property="client" resultMap="Client.clientRM"/>
    ...
</resultMap>
```

is now:

```
<resultMap ...>
    <association property="client" resultMap="Client.clientRM"/>
    ...
</resultMap>
```

## `<parameterMap>` ##
Although this tag is deprecated, it can be used as in iBatis 2.  However for versions up to 3.0.3 there is a bug when using `type="map"` and not specifying `javaType` for a parameter.  This will result in
```
There is no getter for property named '...' in 'interface java.util.Map'    
```

> This should be solved in MyBatis 3.0.4.  For versions 3.0.3 and earlier the workaround is to explicitly specify `javaType`.

## Inline parameters ##

```
#value#
```

is now:

```
#{value}
```

## `jdbcType` changes ##

```
jdbcType="ORACLECURSOR"
```

is now:

```
jdbcType="CURSOR"
```

and

```
jdbcType="NUMBER"
```

is now:

```
jdbcType="NUMERIC"
```

## Stored procedures ##

  * the `<procedure>` tag doesn't exist anymore.  Use `<select>`, `<insert>` or `<update>`.

```
<procedure id="getValues" parameterMap="getValuesPM">
    { ? = call pkgExample.getValues(p_id => ?) }
</procedure>
```

is now:

```
<select id="getValues" parameterMap="getValuesPM" statementType="CALLABLE">
    { ? = call pkgExample.getValues(p_id => ?)}
</select>
```

> If you're calling an insert procedure that returns a value, you can use `<select>` instead of `<insert>`, but make sure to specify `useCache="false"` and `flushCache="true"`.  You'll also have to force a commit.

  * for stored procedures that return a cursor, there is a bug (see [issue 30](https://code.google.com/p/mybatis/issues/detail?id=30)) when using nested result maps (i.e. the output parameter's resultMap contains an `<association>` tag with the `resultMap` attribute).  As long as the issue is not fixed, you have to specify the resultMap of the output parameter on the statement itself as well, or the nested resultMap will not be populated.

## Caching ##

```
<cacheModel id="myCache" type="LRU">
    <flushInterval hours="24"/>
    <property name="size" value="100" />
</cacheModel>
```

is now:

```
<cache flushInterval="86400000" eviction="LRU"/>
```

Note: you can omit `eviction="LRU"` since it is the default.

  * the `<flushOnExecute>` tag is replaced by the `flushCache` attribute for the statements and the cache will be used by all select statements by default.

## Dynamic SQL ##

The most common dynamic SQL in my project is `isNotNull`. Here is an example replacement regex:

Pattern:

```
<isNotNull.*?property=\"(.*?)\">
</isNotNull>
```

Replacement:

```
<if test="$1 != null">
</if>
```

Also common is the use of `isEqual`, you can replace this by a similar `<if>` tag.

# Java code #

## SqlMapClient ##

  * This class doesn't exist anymore.  Use `SqlSessionFactory` instead (see User Guide).

## Custom type handler ##

  * Replace interface `TypeHandlerCallback` with `TypeHandler`.  It has slightly different but similar methods.

## Custom data source factory ##

Old interface:
```
com.ibatis.sqlmap.engine.datasource.DataSourceFactory
```

New interface:
```
org.apache.ibatis.datasource.DataSourceFactory
```

Replace method
```
public void initialize(Map properties)
```

with
```
public void setProperties(Properties props)
```