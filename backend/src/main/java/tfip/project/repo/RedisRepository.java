package tfip.project.repo;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
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
        valueOps.set(telegramUrl, urlLink, 1, TimeUnit.DAYS);
    }

    public String getQRLink(String telegramUrl) {
        ValueOperations<String,String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(telegramUrl);
    }

    // CHAT DATA -- USERNAME / HOST ID (8 char UUID) / TEAM ID

    public void savePlayerByUsername(String username, String hostId, String teamId) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        hashOps.put(username, hostId, teamId);
        redisTemplate.expire(username, 1, TimeUnit.DAYS);
    }

    public String getPlayerHostId(String username) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        if (hashOps.keys(username).size() == 1)
            return hashOps.randomKey(username);
        return null;
    }

    public String getPlayerTeam(String username) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        String hostId = getPlayerHostId(username);
        if (hostId != null)
            return hashOps.get(username, hostId);
        return null;
    }

    public String getPlayerHostAndTeam(String username) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        String hostId = getPlayerHostId(username);
        if (hostId != null) {
            String teamId = hashOps.get(username, hostId);
            return hostId + "/" + teamId.replace(" ", "");
        }
        return null;
    }

    public void deletePlayerByUsername(String username) {
        redisTemplate.delete(username);
    }

    public void deletePlayerByUsername(List<String> usernames) {
        redisTemplate.delete(usernames);
    }

    public boolean playerExists(String username) {
        return redisTemplate.hasKey(username);
    }

    // CHAT DATA -- HOST ID (8 char UUID) / TEAM ID / USERNAMES IN CSV FORMAT

    public void savePlayerByHostId(String hostId, String teamId, String username) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        List<String> playersList = new LinkedList<>();
        String playersInTeamString = hashOps.get(hostId, teamId);
        if (playersInTeamString != null)
            playersList = stringToList(playersInTeamString);
        playersList.add(username);
        hashOps.put(hostId, teamId, listToString(playersList));
        redisTemplate.expire(hostId, 1, TimeUnit.DAYS);
    }

    public List<String> getPlayersInTeam(String hostId, String teamId) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        return stringToList(hashOps.get(hostId, teamId));
    }

    public Map<String,List<String>> getPlayersInTeams(String hostId) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        return hashOps.entries(hostId).entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> stringToList(entry.getValue())
                    ));
    }

    public void deleteHost(String hostId) {
        redisTemplate.delete(hostId);
    }

    public void deletePlayerInHost(String hostId, String oldTeamId, String username) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        List<String> playersList = new LinkedList<>();
        String playersInTeamString = hashOps.get(hostId, oldTeamId);
        if (playersInTeamString != null) {
            playersList = stringToList(playersInTeamString);
            playersList.remove(username);
        }
        String newListString = listToString(playersList);
        hashOps.put(hostId, oldTeamId, newListString);
    }

    public boolean hostExists(String hostId) {
        return redisTemplate.hasKey(hostId);
    }

    public String userTeamInHost(String hostId, String username) {
        HashOperations<String,String,String> hashOps = redisTemplate.opsForHash();
        Map<String, String> teamsMap = hashOps.entries(hostId);
        for (Entry<String, String> teamEntry : teamsMap.entrySet()) {
            List<String> usersList = stringToList(teamEntry.getValue());
            for (String user : usersList) {
                if (user.equals(username))
                    return teamEntry.getKey();
            }
        }
        return null;
    }

    /* HELPER METHODS */

    private String listToString(List<String> list) {
        if (list.isEmpty())
            return "";
        StringBuilder strBuilder = new StringBuilder();
        for (String string : list)
            strBuilder.append(string).append(",");
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        return strBuilder.toString();
    }

    private List<String> stringToList(String string) {
        List<String> list = new LinkedList<>();
        if ("".equals(string) || string == null)
            return list;
        if (!string.contains(",")) {
            list.add(string);
        } else {
            String[] strArray = string.split(",");
            for (String str : strArray)
                list.add(str);
        }
        return list;
    }

}
