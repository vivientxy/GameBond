package tfip.project.repo;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepository {

    @Autowired
    RedisTemplate<String,String> redisTemplate;
    
    // USER RESET LINK

    public void saveResetLink(String resetId, String username) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        valueOps.set(resetId, username, 30, TimeUnit.MINUTES);
    }

    public String validateResetLink(String resetId) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(resetId);
    }

    // TELEGRAM HOST GAME LINK - QR CODE IMAGE

    public void saveQRLink(String telegramUrl, String urlLink) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        valueOps.set(telegramUrl, urlLink);
    }

    public String getQRLink(String telegramUrl) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(telegramUrl);
    }

}
