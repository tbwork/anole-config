package org.tbwork.anole.hub;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class InitSequenceBean  implements InitializingBean {  
    
    public InitSequenceBean() {  
       System.out.println("InitSequenceBean: constructor");  
    }  
     
    @PostConstruct  
    public void postConstruct() {  
       System.out.println("InitSequenceBean: postConstruct");  
    }  
     
    public void initMethod() {  
       System.out.println("InitSequenceBean: init-method");  
    }  
     
    @Override  
    public void afterPropertiesSet() throws Exception {  
       System.out.println("InitSequenceBean: afterPropertiesSet");   
    }  
}  