import com.fasterxml.jackson.databind.ObjectMapper;
import connect.RedisUtil;
import connect.HibenrnateUtil;
import dao.CityDAO;
import entity.City;
import io.lettuce.core.RedisClient;
import org.hibernate.Session;
import redis.RedisService;

import java.util.List;

import static java.util.Objects.nonNull;

public class Menu {
    static final HibenrnateUtil CONNECTION_DATABASE = new HibenrnateUtil();
    static final Session currentSession = CONNECTION_DATABASE.getCurrentSession();
    static final CityDAO cityDAO = new CityDAO(City.class, currentSession.getSessionFactory());
    static final ObjectMapper mapper = new ObjectMapper();
    static final RedisUtil REDIS_UTIL = new RedisUtil();
    static final RedisClient redisClient = REDIS_UTIL.getRedisClient();
    static final RedisService transformator = new RedisService();

    public static void main(String[] args) {
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

        shutdown();
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