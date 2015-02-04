package dao.daoImpl;

import dao.UserDao;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import table.User;

import java.util.List;


public class UserDaoImpl extends HibernateDaoSupport implements UserDao {


    public UserDaoImpl(){}

    @Override
    protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {

        HibernateTemplate result = super.createHibernateTemplate(sessionFactory);
        result.setAllowCreate(false);
        return result;
    }


    @Transactional
    public void addUser(User user) {

        getSession().save(user);
    }


    @Transactional
    public void updateUser(User user) {

        getSession().update(user);
    }

    @Transactional
    public void deleteUser(int id) {

        getSession().delete(getSession().load(User.class, id));
    }


    @Transactional
    public List<User> getAllUsers() {
        return getSession().createCriteria(User.class).list();
    }


}
