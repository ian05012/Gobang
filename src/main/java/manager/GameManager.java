package manager;


import gobang.Game;
import net.dv8tion.jda.api.entities.Member;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.util.*;

public class GameManager {

    public Map<String,Game> map = new HashMap<>();
    public Map<String,String> categoryMap = new HashMap<>();
    public Set<Member> player = new HashSet<>();
    public Map<Member,Game> playerGameMap = new HashMap<>();


    public void load() throws IOException, ParseException {

        File file = new File("category.json");



        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("檔案創建失敗");
            }
        }


        JSONArray jsonArray = null;

        try {
            Object oj = new JSONParser().parse(new FileReader("category.json"));

            jsonArray = (JSONArray) oj;
        } catch (IOException | ParseException ignored) {

        } catch (ClassCastException e){
            JSONObject oj = (JSONObject) new JSONParser().parse(new FileReader("category.json"));

            for(Object st:oj.entrySet()){
                categoryMap.put(st.toString().split("=")[0],st.toString().split("=")[1]);
            }

            return;
        }

        for(Object oj : jsonArray){
            JSONObject json = (JSONObject) oj;
            categoryMap.put((String) json.get("guild"),(String) json.get("category"));
        }


    }

}
