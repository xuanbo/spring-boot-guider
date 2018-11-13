# spring-boot-mongo

> mongodb快速入门

## 介绍

* [mongo入门](#mongo入门)
* [补充](#补充)

## mongo入门

### 依赖

依赖`spring-boot-starter-data-mongodb`模块即可
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
    <version>${spring.boot.version}</version>
</dependency>
```

### 配置

全局配置文件中配置mongodb的连接信息
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://{username}:{password}@{ip}:{port}/{database}
```

修改其中的变量即可

`spring-boot-starter-data-mongodb`模块会自动帮我们配置`MongoTemplate`模板bean，通过它操作mongodb，就像`JdbcTemplate`

### entity

创建一个实体与mongo中的bson对应，类似关系型数据库中的table

创建一个通用父类，包含常用字段
```java
public abstract class Entity implements Serializable {

    @Id
    private String id;
    private Date createAt;
    private Date updateAt;
}
```

具体的实体类继承，`Document`注解相当于JPA中的`Table`注解，映射字段
```java
@Document(collection = "T_Demo")
public class Demo extends Entity {

    private String name;

}
```

### 测试

编写单元测试，做下简单的操作

```java
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void save() {
        Demo demo = new Demo();
        demo.setName("奔波儿灞");
        mongoTemplate.save(demo);
    }

    @Test
    public void find() {
        List<Demo> demos = mongoTemplate.findAll(Demo.class);
        LOG.info("demos: {}", demos);
    }

}
```

通过`MongoTemplate`，我们可以方便的对mongodb进行操作，比mongo driver好用的多

## 补充

### 去掉_class字段

可以发现我们通过`MongoTemplate`操作，会多一个`_class`字段，下面介绍如何去掉。

#### 配置映射

配置一个`MappingMongoConverter`bean，去掉`_class`字段
```java
@Configuration
public class MongoConfiguration {

    /**
     * java对象与mongo bson数据的映射配置，去掉插入数据库后_class字段
     *
     * @param factory MongoDbFactory
     * @param context MongoMappingContext
     * @return MappingMongoConverter
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        // mongodb中不保存_class属性
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingConverter;
    }

}
```

### 通用crud

对基本的增删改查say no

#### 定义接口

抽象出通用接口，实体需要继承`Entity`父类
```java
public interface BaseDao<T extends Entity> {

    /**
     * 根据id查询记录
     *
     * @param id 记录id
     * @return T
     */
    T findById(String id);

    /**
     * 批量获取记录
     *
     * @param ids 记录id集合
     * @return List<T>
     */
    List<T> findByIds(List<String> ids);

    /**
     * 查询一条记录
     *
     * @param query Query
     * @return T
     */
    T findOne(Query query);

    /**
     * 查询多条记录
     *
     * @param query Query
     * @return List<T>
     */
    List<T> find(Query query);

    /**
     * 统计记录条数
     *
     * @param query Query
     * @return Long
     */
    Long count(Query query);

    /**
     * 插入记录
     *
     * @param entity T
     */
    void insert(T entity);

    /**
     * 插入或更新记录
     *
     * @param entity T
     */
    void save(T entity);

    /**
     * 更新所有满足条件的记录
     *
     * @param query Query
     * @param update Update
     */
    void update(Query query, Update update);

    /**
     * 根据id删除记录
     *
     * @param id 记录id
     */
    void deleteById(String id);

    /**
     * 批量删除记录
     *
     * @param ids 记录集合
     */
    void deleteByIds(List<String> ids);

    /**
     * 删除记录
     *
     * @param query Query
     */
    void delete(Query query);

    /**
     * 分页获取数据，传统分页，适用于数据量不是很大的情况，内部采用skip、limit
     *
     * @param criteria 查询条件
     * @param pageable 分页对象
     * @return Page<T>
     */
    Page<T> page(Criteria criteria, Pageable pageable);
}
```

#### 实现

做过`Hibernate`的通用`BaseDao`的人应该特别熟悉，主要是利用范型的基本知识
```java
public abstract class BaseDaoImpl<T extends Entity> implements BaseDao<T> {

    private static final String ID_KEY = "_id";
    private static final String UPDATE_KEY = "updateAt";

    @Autowired
    private MongoTemplate mongoTemplate;

    private Class<T> entityClazz;

    @SuppressWarnings({"unchecked"})
    public BaseDaoImpl() {
        Type genericType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genericType).getActualTypeArguments();
        entityClazz = (Class) params[0];
    }

    @Override
    public T findById(String id) {
        return mongoTemplate.findById(id, entityClazz);
    }

    @Override
    public List<T> findByIds(List<String> ids) {
        return mongoTemplate.find(query(Criteria.where(ID_KEY).in(ids)), entityClazz);
    }

    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, entityClazz);
    }

    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, entityClazz);
    }

    @Override
    public Long count(Query query) {
        return mongoTemplate.count(query, entityClazz);
    }

    @Override
    public void insert(T entity) {
        if (Objects.isNull(entity.getCreateAt())) {
            entity.setCreateAt(new Date());
        }
        if (Objects.isNull(entity.getUpdateAt())) {
            entity.setUpdateAt(new Date());
        }
        mongoTemplate.insert(entity);
    }

    @Override
    public void save(T entity) {
        Date now = new Date();
        if (Objects.isNull(entity.getCreateAt())) {
            entity.setCreateAt(now);
        }
        entity.setUpdateAt(now);
        mongoTemplate.save(entity);
    }

    @Override
    public void update(Query query, Update update) {
        Object updateAt = update.getUpdateObject().get(UPDATE_KEY);
        if (Objects.isNull(updateAt)) {
            update.set(UPDATE_KEY, new Date());
        }
        mongoTemplate.updateMulti(query, update, entityClazz);
    }

    @Override
    public void deleteById(String id) {
        mongoTemplate.remove(query(Criteria.where(ID_KEY).is(id)), entityClazz);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        mongoTemplate.remove(query(Criteria.where(ID_KEY).in(ids)), entityClazz);
    }

    @Override
    public void delete(Query query) {
        mongoTemplate.remove(query, entityClazz);
    }

    @Override
    public Page<T> page(Criteria criteria, Pageable pageable) {
        Query query = Query.query(criteria);
        Long count = count(query);
        if (count == 0L) {
            return new PageImpl<>(Collections.emptyList(), pageable, count);
        }
        List<T> list = find(query.with(pageable));
        return new PageImpl<>(list, pageable, count);
    }

    protected Query query(Criteria criteria) {
        return Query.query(criteria);
    }

    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
```

#### 使用

其他实体类继承`Entity`，编写dao接口、实现类

dao接口特别简单，继承`BaseDao`
```java
public interface DemoDao extends BaseDao<Demo> {
}
```

实现类也特别简单，继承`BaseDaoImpl`
```java
@Repository
public class DemoDaoImpl extends BaseDaoImpl<Demo> implements DemoDao {
}
```

这样就获得了通用的能力

#### 测试

简单测试下
```java
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class DemoDaoTest {

    private static final Logger LOG = LoggerFactory.getLogger(DemoDaoTest.class);

    @Autowired
    private DemoDao demoDao;

    @Test
    public void page() {
        // 第几页从0开始。。
        Pageable page = new PageRequest(0, 10);
        Page<Demo> demoPage = demoDao.page(new Criteria(), page);
        LOG.info("totalPage: {}, totalElements: {}, data: {}", demoPage.getTotalPages(), demoPage.getTotalElements(), demoPage.getContent());
    }

}
```

### controller中优雅的分页

利用`PageableDefault`注解将分页参数反射到`Pageable`的实现类
```java
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping
    public Page<Demo> page(@PageableDefault(page = 0, size = 20) Pageable pageable) {
        return demoService.page(pageable);
    }

}
```

分页参数：`page`、`size`、`sort`

查询第2页，每页1条数据：`http://127.0.0.1:8080/demo?page=1&size=1`

注意：`page`是从0开始的

根据创建时间降序：`http://127.0.0.1:8080/demo?page=0&size=10&sort=createAt,desc`

提示：多个字段进行排序，`sort=字段1,desc|asc&sort=字段2,desc|asc`