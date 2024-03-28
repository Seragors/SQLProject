import com.fasterxml.jackson.databind.ObjectMapper;
import connect.RedisUtil;
import connect.HibenrnateUtil;
import dao.CityDAO;
import entity.City;
import io.lettuce.core.RedisClient;
import org.hibernate.Session;
import redis.RedisService;

import java.util.List;
import java.util.Scanner;

import static java.util.Objects.nonNull;

public class Menu {
    private static final int EXIT = 0;
    private static final int ALL_CITY = 1;
    private static final int CITY_ID = 2;
    private static final String MENU_SELECT = """
            1. All_City
            2. City_Id               
            0. Exit
            """;
    private static boolean isRun = true;
    static final HibenrnateUtil CONNECTION_DATABASE = new HibenrnateUtil();
    static final Session currentSession = CONNECTION_DATABASE.getCurrentSession();
    static final CityDAO cityDAO = new CityDAO(City.class, currentSession.getSessionFactory());
    static final ObjectMapper mapper = new ObjectMapper();
    static final RedisUtil REDIS_UTIL = new RedisUtil();
    static final RedisClient redisClient = REDIS_UTIL.getRedisClient();
    static final RedisService transformator = new RedisService();
    Scanner scanner = new Scanner(System.in);

    public void open() {
        while (isRun) {
            System.out.println(MENU_SELECT);
            int number = scanner.nextInt();
            if (number == ALL_CITY) {
                System.out.println(ALL_CITY);
                List<City> all = cityDAO.fetchData();
                transformator.pushToRedis(transformator.transformData(all), redisClient, mapper);
                List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);
                long startRedis = System.currentTimeMillis();
                REDIS_UTIL.RedisData(ids, redisClient, mapper);
                long stopRedis = System.currentTimeMillis();

                long startMysql = System.currentTimeMillis();
                CONNECTION_DATABASE.MySQLData(ids, cityDAO);
                long stopMysql = System.currentTimeMillis();

                System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
                System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));
            } else if (number == CITY_ID) {
                System.out.println(CITY_ID);
                System.out.println("Enter the database ID");
                int numberCityId = scanner.nextInt();
                cityDAO.findById(numberCityId);
            } else if (number == EXIT) {
                shutdown();
                isRun = false;
                System.out.println("By!");
            }
        }
    }

    private static void shutdown() {
        if (nonNull(currentSession)) {
            currentSession.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }
}