package com.example.springboot.mongo.dao.impl;

import com.example.springboot.mongo.dao.BaseDao;
import com.example.springboot.mongo.entity.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
