package lovecalculator.app.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lovecalculator.app.model.Result;

@Configuration
public class RedisConfig {
  // redis host value from application.properties
  @Value("${spring.redis.host}")
  private String redisHost;

  // redis port value from application.properties
  @Value("${spring.redis.port}")
  private Optional<String> redisPort;

  @Value("${spring.redis.username}")
  private String redisUsername;

  @Value("${spring.redis.password}")
  private String redisPassword;

  // define redis template bean as single object throughout the runtime. Return the RedisTemplate
  @Bean
  @Scope("singleton")
  public RedisTemplate<String, Result> redisTemplate(){
    final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(redisHost);
    config.setPort(Integer.parseInt(redisPort.get()));
    if(redisUsername.isEmpty() && redisPassword.isEmpty()){
      config.setUsername(redisUsername);
      config.setPassword(redisPassword);
    }

    final JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();
    final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
    jedisFac.afterPropertiesSet();

    RedisTemplate<String, Result> redisTemplate = new RedisTemplate<String, Result>();
    // associate with the redis connection
    redisTemplate.setConnectionFactory(jedisFac); 
    Jackson2JsonRedisSerializer jackson2Serializer = new Jackson2JsonRedisSerializer(Result.class); // force-cast
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    // set the map key-value serialization type to string
    redisTemplate.setHashKeySerializer(redisTemplate.getKeySerializer());
    // enable redis to store java object on the value column
    RedisSerializer<Object> objSerializer = new JdkSerializationRedisSerializer(getClass().getClassLoader());
    redisTemplate.setValueSerializer(jackson2Serializer);
    redisTemplate.setHashValueSerializer(jackson2Serializer);


    return redisTemplate;
  }
}

