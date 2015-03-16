# FAQ #

### What is the difference between #{...} and ${...}? ###
MyBatis interprets #{...} as a parameter marker in a JDBC prepared statement. MyBatis interprets ${...} as string substitution.  It is important to know the difference because parameter markers cannot be used in certain places in SQL statements.
For example, you cannot use a parameter marker to specify a table name.        Given the following code:
```
Map<String, Object> parms = new HashMap<String, Object>();
parms.put("table", "foo");
parms.put("criteria", 37);
List<Object> rows = mapper.generalSelect(parms);
```
```
<select id="generalSelect" parameterType="map">
  select * from ${table} where col1 = #{criteria}
</select>
```
MyBatis will generate the following prepared statement:
```
select * from foo where col1 = ?
```
**Important**: note that use of ${...} (string substitution) presents a risk for SQL injection attacks. Also, string substitution can be problematical for complex types like dates. For these reasons, we recommend using the #{...} form whenever possible.

### How do I code an SQL LIKE? ###
There are two methods.  In the first (and preferred) method, you append
the SQL wildcards in your Java code. For example:
```
String wildcardName = "%Smi%";
List<Name> names = mapper.selectLike(wildcardName);
```
```
<select id="selectLike">
  select * from foo where bar like #{value}
</select>
```
Another method is to concatenate the wildcards in your SQL. This method
is less safe than the method above because of possible SQL injection.           For example:
```
String wildcardName = "Smi";
List<Name> names = mapper.selectLike(wildcardName);
```
```
<select id="selectLike">
  select * from foo where bar like '%' || '${value}' || '%'
</select>
```
**Important**: Note the use of $ vs. # in the second example!

### How do I code a batch insert? ###
First, code a simple insert statement like this:
```
<insert id="insertName">
  insert into names (name) values (#{value})
</insert>
```
Then execute a batch in Java code like this:
```
List<String> names = new ArrayList<String>();
names.add("Fred");
names.add("Barney");
names.add("Betty");
names.add("Wilma");
          
SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
try {
  NameMapper mapper = sqlSession.getMapper(NameMapper.class);
  for (String name : names) {
    mapper.insertName(name);
  }
  sqlSession.commit();
} finally {
  sqlSession.close();
}
```

### How can I retrieve the value of an auto generated key? ###
The insert method **always** returns an int - which is the number of rows inserted. Auto generated key values are placed into the parameter object and are available after the completion of the insert method.  For example:
```
<insert id="insertName" useGeneratedKeys="true" keyProperty="id">
  insert into names (name) values (#{name})
</insert>
```
```
Name name = new Name();
name.setName("Fred");
          
int rows = mapper.insertName(name);
System.out.println("rows inserted = " + rows);
System.out.println("generated key value = " + name.getId());
```

### How do I use multiple parameters in a mapper? ###
Java reflection does not provide a way to know the name of a method parameter so MyBatis names them by default like: param1, param2...
If you want to give them a name use the @param annotation this way:
```
import org.apache.ibatis.annotations.Param;
public interface UserMapper {
   User selectUser(@Param("username") String username, @Param("hashedPassword") String hashedPassword);
}
```
Now you can use them in your xml like follows:
```
<select id=”selectUser” resultType=”User”>
  select id, username, hashedPassword
  from some_table
  where username = #{username}
  and hashedPassword = #{hashedPassword}
</select>
```