package dao;

import entity.City;
import org.hibernate.*;
import org.hibernate.query.Query;

import java.util.ArrayList;
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
        int totalCount = getTotalCount().intValue();
        Transaction transaction = getCurrentSession().beginTransaction();
        Query query = getCurrentSession().createQuery("SELECT c FROM City c ", base);
        ScrollableResults resultScroll = query.scroll(ScrollMode.FORWARD_ONLY);
        if (resultScroll.next()) {
            int step = 0;
            while (totalCount > step++) {
                resultList.add(((T) resultScroll.get(0)));
                if (!resultScroll.next()) {
                    break;
                }
            }
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
        Query<T> query = getCurrentSession().createQuery("SELECT c FROM City c WHERE c.id = :id ", base);
        query.setParameter("id", id);
        City city = (City) query.getSingleResult();
        transaction.commit();
        return (List<T>) List.of(city);
    }
}