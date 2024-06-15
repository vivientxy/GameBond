package tfip.project.repo;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
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

    // QR CODE IMAGE - TELEGRAM HOST GAME LINK

    public void saveQRLink(String telegramUrl, String urlLink) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        valueOps.set(telegramUrl, urlLink);
    }

    public String getQRLink(String telegramUrl) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(telegramUrl);
    }

    // CHAT DATA -- HOST ID / CHAT ID / TEAM ID

    public void savePlayerTeam(String hostId, Long chatId, String teamId) {
        HashOperations<String,Long,String> hashOps = redisTemplate.opsForHash();
        hashOps.put(hostId, chatId, teamId);
    }

    public String getPlayerTeam(String hostId, Long chatId) {
        HashOperations<String,Long,String> hashOps = redisTemplate.opsForHash();
        return hashOps.get(hostId, chatId);
    }

}
