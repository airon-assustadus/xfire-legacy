package org.codehaus.xfire.spring;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

import org.apache.xbean.spring.context.SpringApplicationContext;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.spring.editors.ServiceFactoryEditor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

class GenericApplicationContext extends org.springframework.context.support.GenericApplicationContext
    implements SpringApplicationContext
{
	
	private static final String XFIRE_SERVICE_REGISTRY = "xfire.serviceRegistry";
	private static final String XFIRE_TRANSPORT_MANAGER = "xfire.transportManager";
	private static final String XFIRE = "xfire";
	private static final String XFIRE_TYPE_MAPPING_REGISTRY = "xfire.typeMappingRegistry";
	private static final String XFIRE_AEGIS_BINDING_PROVIDER = "xfire.aegisBindingProvider";
	private static final String XFIRE_SERVICE_FACTORY = "xfire.serviceFactory";
	private static final String XFIRE_SERVLET_CONTROLLER = "xfire.servletController";
	private static final String XFIRE_MESSAGE_SERVICE_FACTORY = "xfire.messageServiceFactory";
	private static final String XFIRE_MESSAGE_BINDING_PROVIDER = "xfire.messageBindingProvider";
	private static final String XFIRE_CUSTOM_EDITOR_CONFIGURER = "xfire.customEditorConfigurer";
	private static final String XFIRE_SERVICE_FACTORY_EDITOR = "xfire.serviceFactoryEditor";

    public GenericApplicationContext()
    {
        super();
    }

    public GenericApplicationContext(ApplicationContext arg0)
    {
        super(arg0);
        
        registerXFireTransportManager();
        registerXFireServiceRegistry();
        registerXFire();
        registerXFireTypeMappingRegistry();
        registerXFireAegisBindingProvider();
        registerXFireServiceFactory();
        registerXFireServletController();
        registerXFireMessageServiceFactory();
        registerXFireMessageBindingProvider();
        registerXFireServiceFactoryEditor();
        registerXFireCustomEditorConfigurer();
        
        
    }

    public GenericApplicationContext(DefaultListableBeanFactory arg0, ApplicationContext arg1)
    {
        super(arg0, arg1);
    }

    public GenericApplicationContext(DefaultListableBeanFactory arg0)
    {
        super(arg0);
    }

	public void destroy() throws Exception {
	}
	
	private BeanDefinitionBuilder registerXFireTransportManager() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.transport.DefaultTransportManager");
		bean.setDestroyMethodName("dispose");
		bean.setInitMethodName("initialize");
        registerBeanDefinition(XFIRE_TRANSPORT_MANAGER, bean.getBeanDefinition());
        return bean;
	}
	
	private BeanDefinitionBuilder registerXFireServiceRegistry() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.service.DefaultServiceRegistry");
		registerBeanDefinition(XFIRE_SERVICE_REGISTRY, bean.getBeanDefinition());
		return bean;
	}
	
	private BeanDefinitionBuilder registerXFire() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.DefaultXFire");
		bean.addConstructorArgReference(XFIRE_SERVICE_REGISTRY);
		bean.addConstructorArgReference(XFIRE_TRANSPORT_MANAGER);
		registerBeanDefinition(XFIRE, bean.getBeanDefinition());
		return bean;
	}
	
	private BeanDefinitionBuilder registerXFireTypeMappingRegistry() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.aegis.type.DefaultTypeMappingRegistry");
		bean.setInitMethodName("createDefaultMappings");
        registerBeanDefinition(XFIRE_TYPE_MAPPING_REGISTRY, bean.getBeanDefinition());
        return bean;
	}
	
	private BeanDefinitionBuilder registerXFireAegisBindingProvider() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.aegis.AegisBindingProvider");
		bean.addConstructorArgReference(XFIRE_TYPE_MAPPING_REGISTRY);
		registerBeanDefinition(XFIRE_AEGIS_BINDING_PROVIDER, bean.getBeanDefinition());
		return bean;
	}
	
	private BeanDefinitionBuilder registerXFireServiceFactory() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.service.binding.ObjectServiceFactory");
		bean.addConstructorArgReference(XFIRE_TRANSPORT_MANAGER);
		bean.addConstructorArgReference(XFIRE_AEGIS_BINDING_PROVIDER);
        registerBeanDefinition(XFIRE_SERVICE_FACTORY, bean.getBeanDefinition());
        return bean;
	}
	
	private BeanDefinitionBuilder registerXFireServletController() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.transport.http.XFireServletController");
		bean.addConstructorArgReference(XFIRE);
		registerBeanDefinition(XFIRE_SERVLET_CONTROLLER, bean.getBeanDefinition());
		return bean;
	}
	
	private BeanDefinitionBuilder registerXFireMessageServiceFactory() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.service.binding.ObjectServiceFactory");
		bean.addConstructorArgReference(XFIRE_TRANSPORT_MANAGER);
		bean.addConstructorArgReference(XFIRE_MESSAGE_BINDING_PROVIDER);
        registerBeanDefinition(XFIRE_MESSAGE_SERVICE_FACTORY, bean.getBeanDefinition());
        return bean;
	}
	
	private BeanDefinitionBuilder registerXFireMessageBindingProvider() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.service.binding.MessageBindingProvider");
		registerBeanDefinition(XFIRE_MESSAGE_BINDING_PROVIDER, bean.getBeanDefinition());
		return bean;
	}
	
	private BeanDefinitionBuilder registerXFireServiceFactoryEditor() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.codehaus.xfire.spring.editors.ServiceFactoryEditor");
		bean.addPropertyReference("transportManager", XFIRE_TRANSPORT_MANAGER);
		registerBeanDefinition(XFIRE_SERVICE_FACTORY_EDITOR, bean.getBeanDefinition());
		return bean;
	}
	
	private BeanDefinitionBuilder registerXFireCustomEditorConfigurer() {
		BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.beans.factory.config.CustomEditorConfigurer");
		Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap<Class<?>, Class<? extends PropertyEditor>>();
		customEditors.put(ServiceFactory.class, ServiceFactoryEditor.class);
		bean.addPropertyValue("customEditors", customEditors);
		registerBeanDefinition(XFIRE_CUSTOM_EDITOR_CONFIGURER, bean.getBeanDefinition());
		return bean;
	}
}
