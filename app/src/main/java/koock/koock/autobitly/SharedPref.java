package koock.koock.autobitly;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref {

    //pattern : singlton class
    private static SharedPref sharedPref = null;

    static private String SHARE_NAME = "SHARE_PREF";
    static SharedPreferences sharedPreferences = null;
    static SharedPreferences.Editor editor = null;

    private SharedPref(Context context){
        sharedPreferences = context.getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPref getInstance(Context context){
        if(sharedPref == null){
            sharedPref = new SharedPref(context);
        }
        return sharedPref;
    }

    public void saveToken(String token){
        editor.putString("token",token);
        editor.apply();
    }

    public void deleteToken(String token){
        editor.remove("token");
        editor.commit();
    }

    public String getToken(){
        return sharedPreferences.getString("token","acess token을 입력해주세요");
    }
}