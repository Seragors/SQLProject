package dao;

import entity.City;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class EntityDAO<T> {
    private final Class<T> base;

    private SessionFactory sessionFactory;

    public EntityDAO(Class<T> base, SessionFactory sessionFactory) {
        this.base = base;
        this.sessionFactory = sessionFactory;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<T> getItems(int offset, int limit) {
        Query query = getCurrentSession().createQuery("FROM " + base.getName(), base);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public Long getTotalCount() {
        Transaction transaction = getCurrentSession().beginTransaction();
        Long singleResult = (Long) getCurrentSession().createQuery("SELECT count(*) FROM " + base.getName()).getSingleResult();
        transaction.commit();
        return singleResult;
    }

    public List<T> fetchData() {
        List<T> resultList = new ArrayList<>();
        int step = 500;
        int totalCount = getTotalCount().intValue();
        Transaction transaction = getCurrentSession().beginTransaction();
        for (int i = 0; i < totalCount; i += step) {
            resultList.addAll(getItems(i, step));
        }
        transaction.commit();
        return resultList;
    }

    public T getById(final int id) {
        Optional<T> index = Optional.of(getCurrentSession().get(base, id));
        return index.orElse(null);
    }

    public List<T> findById(Integer id) {
        Transaction transaction = getCurrentSession().beginTransaction();
        Query<T> query = getCurrentSession().createQuery("SELECT c FROM City c WHERE c.id = :id", base);
        query.setParameter("id", id);
        City city = (City) query.getSingleResult();
        transaction.commit();
        return (List<T>) List.of(city);
    }
}