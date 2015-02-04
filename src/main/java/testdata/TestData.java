package testdata;

import dao.UserDao;
//import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import table.User;

import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Created by Александр on 05.11.14.
 */
public class TestData {

    public static void main(String[] args)
    {

       // Logger logger = Logger.getLogger(TestData.class);
       // logger.debug("Hello");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"}, true);
        UserDao userDao1 = (UserDao)context.getBean("userDao");

        String[] names = {"Alexey", "Alexander", "Andrey", "Boris", "Mariya", "Victor",
                "Diana", "Daria", "Vitalii", "Vladimir", "Tatiana", "Elena",
                "Karina", "Oleg", "Dmitry", "Valentin", "Vlad", "Anna", "Michail",
                "Sergey", "Anton", "Maksim", "Yana", "Marina", "Yulia"};
        Random rand = new Random();

        User user = new User();
        for (int i = 0; i < 200; i++)
        {
            user.setName(names[rand.nextInt(names.length-1)]);
           // user.setName("Mary");
            user.setAge(rand.nextInt(200));
            user.setAdmin(rand.nextBoolean());
            //user.setAge(20);
           // user.setAdmin(true);
            user.setCreateDate(new GregorianCalendar());
            userDao1.addUser(user);
        }


    }
}
