import com.fasterxml.jackson.databind.ObjectMapper;
import connect.ClientRedisDataBase;
import connect.SessionDataBase;
import dao.CityDAO;
import entity.City;
import io.lettuce.core.RedisClient;
import org.hibernate.Session;
import redis.RedisService;

import java.util.List;

import static java.util.Objects.nonNull;

public class Main {
    static final SessionDataBase CONNECTION_DATABASE = new SessionDataBase();
    static final Session currentSession = CONNECTION_DATABASE.getCurrentSession();
    static final CityDAO cityDAO = new CityDAO(City.class, currentSession.getSessionFactory());
    static final ObjectMapper mapper = new ObjectMapper();
    static final ClientRedisDataBase clientRedisDataBase = new ClientRedisDataBase();
    static final RedisClient redisClient = clientRedisDataBase.prepareRedisClient();
    static final RedisService transformator = new RedisService();

    public static void main(String[] args) {
        List<City> all = cityDAO.findAll();
        transformator.pushToRedis(transformator.transformData(all), redisClient, mapper);
        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        clientRedisDataBase.testRedisData(ids, redisClient, mapper);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        CONNECTION_DATABASE.testMySQLData(ids, cityDAO);
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