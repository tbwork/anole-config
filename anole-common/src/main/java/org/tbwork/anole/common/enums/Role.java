package org.tbwork.anole.common.enums;

public enum Role {

	STRANGER("stranger", 0), // 陌生人：可以看Key，但是看不到value； 
	VISTOR("vistor", 1),     // 访客：可以看key和value；
	MANAGER("manager", 2),   // 管理人：可以编辑配置。 
	OWNER("owner",3),        // 所有者：可以删除配置。（注意拥有者具有所有环境的管理员权限）
	ADMIN("admin",4),        // 管理员：所有配置的owner
	UNKNOWN("not_logined", -1); // 未知：未登录的用户       
	
	private String name;
	private int value;
	
	private Role(String name, int value){
		this.value = value;
		this.name = name;
	}
	
	public int value(){
		return value;
	}
	
	public String _name(){
		return name;
	}
	
	public static Role getRoleByValue(int value){
		for( Role item : Role.values()){
			if(item.value == value){
				return item;
			}
		}
		return STRANGER;
	}
}
