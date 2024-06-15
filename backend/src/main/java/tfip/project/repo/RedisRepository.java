package tfip.project.repo;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepository {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    // USER RESET LINK (18 chars UUID)

    public void saveResetLink(String resetId, String username) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        valueOps.set(resetId, username, 30, TimeUnit.MINUTES);
    }

    public String validateResetLink(String resetId) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(resetId);
    }

    // QR CODE IMAGE - TELEGRAM HOST GAME LINK (entire link with payload: Base64-encoded 8 char UUID - hostId)

    public void saveQRLink(String telegramUrl, String urlLink) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        valueOps.set(telegramUrl, urlLink);
    }

    public String getQRLink(String telegramUrl) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(telegramUrl);
    }

    // CHAT DATA -- CHAT ID (Long) / HOST ID (8 char UUID) / TEAM ID

    public void savePlayerInfo(Long chatId, String hostId, String teamId) {
        String chatIdString = String.valueOf(chatId);
        savePlayerTeam(chatIdString, hostId, teamId);
        savePlayersInHost(hostId, chatIdString);
    }

    private void savePlayerTeam(String chatIdString, String hostId, String teamId) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        hashOps.put(chatIdString, hostId, teamId);
    }

    private void savePlayersInHost(String hostId, String chatId) {
        ListOperations<String,String> listOps = redisTemplate.opsForList();
        listOps.leftPush(hostId, chatId);
    }

    public String getPlayerTeam(Long chatId, String hostId) {
        String chatIdString = String.valueOf(chatId);
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        return hashOps.get(chatIdString, hostId);
    }

    // public List<String> getPlayersInHost(String hostId) {
    //     ListOperations<String,String> listOps = redisTemplate.opsForList();
    //     return listOps.range(hostId, 0, listOps.size(hostId));
    // }

    public void deletePlayer(Long chatId, String hostId) {
        String chatIdString = String.valueOf(chatId);
        redisTemplate.delete(chatIdString);
        ListOperations<String,String> listOps = redisTemplate.opsForList();
        listOps.remove(hostId, 1, chatIdString);
    }

    public void deleteGame(String hostId) {
        ListOperations<String,String> listOps = redisTemplate.opsForList();
        List<String> playerList = listOps.rightPop(hostId, listOps.size(hostId));
        redisTemplate.delete(playerList);
        redisTemplate.delete(hostId);
    }


}
