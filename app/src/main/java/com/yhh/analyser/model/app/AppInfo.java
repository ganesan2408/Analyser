/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.model.app;

import android.graphics.drawable.Drawable;

/**
 * details of app informance
 * 
 */
public class AppInfo implements Comparable<AppInfo> {
	private String name;
	private String packageName;
	private Drawable logo;
	private String versionName;
	private int pid;
	private int uid;
	private String firstLetter;  //name的首字母
    private boolean isSystem;

    public boolean isSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

    public Drawable getLogo() {
        return logo;
    }

    public void setLogo(Drawable logo) {
        this.logo = logo;
    }
    
    public String getVersionName() {
        return versionName;
    }
    
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    
    public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
	
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
    
    public String getFirstLetter(){
        return firstLetter;
    }
    
    public void setFirstLetter(String firstLetter){
        this.firstLetter = firstLetter;
    }

	@Override
	public int compareTo(AppInfo arg0) {
		return (this.getName().compareTo(arg0.getName()));
	}
}
