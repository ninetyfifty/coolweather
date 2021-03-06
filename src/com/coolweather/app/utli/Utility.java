package com.coolweather.app.utli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;


public class Utility {
	
	public static void handleWeatherResponse(Context context, String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherInfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityId");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	} 

	private static void saveWeatherInfo(Context context,String cityName, String weatherCode,
			String temp1, String temp2, String weatherDesp, String publishTime) {
		SimpleDateFormat sdf=new SimpleDateFormat("YYYY年M月D日",Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit(); 
		editor.putBoolean("city_selected", true);
		editor.putString("city_name",cityName);
		editor.putString("weather_code",weatherCode );
		editor.putString("temp1",temp1 );
		editor.putString("temp2",temp2 );
		editor.putString("weather_desp",weatherDesp );
		editor.putString("publish_time",publishTime );
		editor.putString("publish_date",sdf.format(new Date()));
		editor.commit();
	}

	/**
	 * 处理网络中返回的省级信息
	 *
	 */
	public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(",");
			if(allProvinces!=null&&allProvinces.length>0){
				for (String p : allProvinces) {
					Province province=new Province();
					String[] provinceText=p.split("\\|");
					province.setProvinceName(provinceText[0]);
					province.setProvinceCode(provinceText[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 处理网络中返回的市级信息
	 *
	 */
	public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB, String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(",");
			if(allCities!=null&&allCities.length>0){
				for (String p : allCities) {
					City city=new City();
					String[] cityText=p.split("\\|");
					city.setCityName(cityText[0]);
					city.setCityCode(cityText[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 处理网络中返回的县级信息
	 *
	 */
	public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties=response.split(",");
			if(allCounties!=null&&allCounties.length>0){
				for (String p : allCounties) {
					County county=new County();
					String[] countyText=p.split("\\|");
					county.setCountyName(countyText[0]);
					county.setCountyCode(countyText[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
}
