package com.liu.Account.network.utils;

/**
 * 打印Log的类型枚举
 * level 表示打印的等级
 */
public enum LogType {
	
    DEBUG(1), WARNING(2), ERROR(3), NEVER(10);
    
    int level;
    
    private LogType(int level){
    	this.level = level;
    }
    
}
