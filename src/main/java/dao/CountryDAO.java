package dao;

import entity.Country;
import org.hibernate.SessionFactory;

public class CountryDAO extends EntityDAO<Country> {
    public CountryDAO(Class<Country> base, SessionFactory sessionFactory) {
        super(base, sessionFactory);
    }
}