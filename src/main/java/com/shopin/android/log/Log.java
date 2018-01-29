package com.shopin.android.log;

import com.orhanobut.logger.Logger;

import java.util.Map;

public class Log {
	/**
	 *  public static final int VERBOSE = 2;
    	public static final int DEBUG = 3;
    	public static final int INFO = 4;
    	public static final int WARN = 5;
    	public static final int ERROR = 6;
    	public static final int ASSERT = 7;
	 */
	//全局设置 日志默认是否存入sdcard
	private static final boolean isDefaultSaveSDCard = LoggerManager.isIsDefaultSaveSDCard();

//	private static final ThreadLocal<boolean> localSaveSDcard = new ThreadLocal<>();
	/**
	 *
	 * @param tag
	 * 		tag
	 * @param log
	 * 		文本信息
	 */
	public static void i(String tag, String log){
		Logger.t(tag).i(log);
	}

	public static void i(String log){
		i(log,isDefaultSaveSDCard);
	}
	public static void i(String log,boolean isSaveSDCard){
		if(isSaveSDCard){
			Logger.t(LoggerManager.TAG_SAVE_SDCARD).i(log);
		}else {
			Logger.i(log);
		}
	}
	/**
	 *
	 * @param tag
	 * 		tag
	 * @param log
	 * 		文本信息
	 */
	public static void d(String tag, String log){
		Logger.t(tag).d(log);
	}

	public static void d(String log,boolean isSaveSDCard){
		if(isSaveSDCard){
			Logger.t(LoggerManager.TAG_SAVE_SDCARD).d(log);
		}else{
			Logger.d(log);
		}
	}
	public static void d(String log){
		d(log,isDefaultSaveSDCard);
	}
	/**
	 *
	 * @param tag
	 * 		tag
	 * @param log
	 * 		文本信息
	 */
	public static void v(String tag, String log){
		Logger.t(tag).v(log);
	}

	public static void v(String log){
		v(log,isDefaultSaveSDCard);
	}

	public static void v(String log,boolean isSaveSDCard){
		if(isSaveSDCard) {
			Logger.t(LoggerManager.TAG_SAVE_SDCARD).v(log);
		}else{
			Logger.v(log);
		}
	}


	/**
	 *
	 * @param tag
	 * 		tag
	 * @param log
	 * 		文本信息
	 */
	public static void w(String tag, String log){
		Logger.t(tag).w(log);
	}

	/**
	 * 默认TAG
	 * @param
	 * 	log
	 */
	public static void w(String log){
		w(log,isDefaultSaveSDCard);
	}

	public static void w(String log,boolean isSaveSDCard){
		if(isSaveSDCard){
			Logger.t(LoggerManager.TAG_SAVE_SDCARD).w(log);
		}else {
			Logger.w(log);
		}
	}
	/**
	 *
	 * @param tag
	 * 		tag
	 * @param log
	 * 		文本信息
	 */
	public static void e(String tag, String log){
		Logger.t(tag).e(log);
	}

	/**
	 * 错误日志默认保存SDCard中
	 * @param log
	 * @param args
	 */
	public static void e(String log,Object ... args){
		Logger.t(LoggerManager.TAG_SAVE_SDCARD).e(log,args);
	}

	/**
	 * 打印Exception并保存在SDcard中
	 * @param throwable
	 * 	错误信息
	 * @param message
	 * 	错误信息描述
	 * @param args
	 */
	public static void e(Throwable throwable,String message,Object...args){
		Logger.t(LoggerManager.TAG_SAVE_SDCARD).e(throwable,message,args);
	}


	/**
	 * 打印对象 例如 map
	 * @param object
	 */
	public static void d(Object object){
		d(object,isDefaultSaveSDCard);
	}

	public static void d(Object object,boolean isSaveSDCard){
		if(isSaveSDCard) {
			Logger.t(LoggerManager.TAG_SAVE_SDCARD).d(object);
		}else{
			Logger.d(object);
		}
	}




	public static void json(String json){
		json(json,isDefaultSaveSDCard);
	}
	/**
	 * 格式化json打印
	 * @param json
	 * @param  isSaveSDCard 是否保存在sdcard
	 */
	public static void json(String json,boolean isSaveSDCard){
		if(isSaveSDCard) {
			Logger.t(LoggerManager.TAG_SAVE_SDCARD).json(json);
		}else{
			Logger.json(json);
		}
	}


	/**
	 * 打印map数据
	 * @param map
	 */
	public static void map(Map map){
		map(map,isDefaultSaveSDCard);
	}

	public static void map(Map map,boolean isSaveSDcard){
		if(isSaveSDcard) {
			Logger.t(LoggerManager.TAG_SAVE_SDCARD).d(map);
		}else{
			Logger.d(map);
		}
	}

	/**
	 * 格式化xml打印
	 * @param xml
	 */
	public static void xml(String xml){
		xml(xml,isDefaultSaveSDCard);
	}

	public static void xml(String xml,boolean isSaveSDcard){
		if(isSaveSDcard) {
			Logger.t(LoggerManager.TAG_SAVE_SDCARD).xml(xml);
		}else{
			Logger.xml(xml);
		}
	}

}
