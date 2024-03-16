package dao;

import entity.CountryLanguage;
import org.hibernate.SessionFactory;

public class CountryLanguageDAO extends EntityDAO<CountryLanguage>{
    public CountryLanguageDAO(Class<CountryLanguage> base, SessionFactory sessionFactory) {
        super(base, sessionFactory);
    }
}