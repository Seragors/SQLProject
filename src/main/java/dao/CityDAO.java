package dao;

import entity.City;
import org.hibernate.SessionFactory;

public class CityDAO extends EntityDAO<City> {
    public CityDAO(Class<City> base, SessionFactory sessionFactory) {
        super(base, sessionFactory);
    }
}