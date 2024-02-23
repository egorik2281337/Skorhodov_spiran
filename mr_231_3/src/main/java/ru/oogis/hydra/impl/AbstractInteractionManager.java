package ru.oogis.hydra.impl;

import org.apache.camel.Component;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.BeanDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.ComponentResolver;
import ru.oogis.hydra.api.InteractionManager;
import ru.oogis.hydra.config.ChannelConfig;
import ru.oogis.hydra.config.ChannelStateInfo;
import ru.oogis.hydra.config.ManagerConfig;
import ru.oogis.hydra.config.sort.ChannelStartupComparator;
import ru.oogis.hydra.exception.ChannelNotFoundException;
import ru.oogis.hydra.jms.ManagerDeploymentEvent;
import ru.oogis.hydra.logging.HydraLogger;
import ru.oogis.hydra.util.HydraHelper;
import ru.oogis.hydra.util.MessageKey;
import ru.oogis.hydra.util.Messages;

import javax.ejb.AccessTimeout;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;
import static ru.oogis.hydra.util.HydraHelper.*;
import static ru.oogis.hydra.util.MessageKey.*;
import static ru.oogis.hydra.util.Messages.getMessage;

public abstract class AbstractInteractionManager implements InteractionManager {

  // private static final String CONTEXT_SUFFIX = "-context";
  // private static final String DLC_SUFFIX = "-deadletter";

  private static final String LOGGER_PACKAGE = "ru.oogis.hydra.";
  private static final String SUPERVISOR_ROUTE = "svrt-";

  //	private static final String PARAM_PATTERN = Pattern.quote("$$"); //$NON-NLS-1$
  //	private static final String PARAM_PATTERN = "[$${*}]"; //$NON-NLS-1$
  private static final String PATH_TO_RESOURCE = "{0}/{1}.xml"; //$NON-NLS-1$
  private static final String PATH_TO_RESOURCES = "/hydra/{0}/"; //$NON-NLS-1$
  private static final String PATH_TO_ROUTES = PATH_TO_RESOURCES + "routes"; //$NON-NLS-1$
  private static final String ROUTE_ID_DELIMITER = "."; //$NON-NLS-1$
  private static final String ROUTE_ID_PREFIX = "route id=\""; //$NON-NLS-1$
  private static final String XML_EXT = ".xml"; //$NON-NLS-1$
  private static final String CLEAN_INTERVAL = "cleanInterval";
  private static final String MAX_STORAGE_TIME = "maxStorageTime";
  protected DefaultCamelContext camelContext;
  protected HydraLogger log;
  private List<ChannelConfig> channelTemplates;
  private ManagerConfig configuration;
  private Map<String, String> routeTemplates;
  private String version;

  @Override
  public void activateChannel(ChannelConfig p_channelConfig) {
    if (p_channelConfig != null) {
      p_channelConfig.setEnabled(true);
      startChannel(p_channelConfig);
      saveConfig();
    }
  }

  @Override
  public void activateChannel(String p_channelId)
    throws ChannelNotFoundException {
    activateChannel(findChannel(p_channelId));
  }

  @Override
  public void addChannel(ChannelConfig p_channelConfig) {
    if (p_channelConfig != null) {
      configuration.getChannels().add(p_channelConfig);
      saveConfig();
      log.logInfo(getId(), p_channelConfig.getId(),
        getMessage(MSG_CHANNEL_ADDED));
    }
  }

  @Override
  @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
  public void applyChannel(ChannelConfig p_channelConfig) {
    if (p_channelConfig != null) {
      stopChannel(p_channelConfig);
      saveConfig();
      if (p_channelConfig.isEnabled()) {
        startChannel(p_channelConfig);
      }
    }
  }

  @Override
  @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
  public void applyConfiguration() {
    saveConfig();
  }

  @Override
  public ChannelConfig createChannel(ChannelConfig p_configTemplate) {
    if (p_configTemplate == null) {
      throw new IllegalArgumentException(
        getMessage(ERR_ILLEGAL_CHANNEL_TEMPLATE));
    }
    ChannelConfig a_result = new ChannelConfig(p_configTemplate);
    int a_channelNumber = getConfiguration().getCounter();
    a_result.setStartupOrder(a_result.getStartupOrder() + a_channelNumber);
    a_result.setId(getId() + ROUTE_ID_DELIMITER + p_configTemplate.getId()
      + ROUTE_ID_DELIMITER + a_channelNumber);
    a_result.setDisplayName(null);
    a_result.setDescription(null);
    a_result.setOwner(getConfiguration());
    return a_result;
  }

  @Override
  @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
  public void deactivateChannel(ChannelConfig p_channelConfig) {
    if (p_channelConfig != null) {
      p_channelConfig.setEnabled(false);
      stopChannel(p_channelConfig);
      saveConfig();
    }
  }

  @Override
  @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
  public void deactivateChannel(String p_channelId)
    throws ChannelNotFoundException {
    deactivateChannel(findChannel(p_channelId));
  }

  @Override
  @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
  public ChannelConfig findChannel(String p_channelId)
    throws ChannelNotFoundException {
    if (configuration != null && p_channelId != null) {
      List<ChannelConfig> a_channels = configuration.getChannels();
      if (a_channels != null) {
        for (ChannelConfig a_config : a_channels) {
          if (p_channelId.equals(a_config.getId())) {
            return a_config;
          }
        }
      }
    }
    throw new ChannelNotFoundException(p_channelId);
  }

  @Override
  public List<ChannelConfig> getChannelTemplates() {
    return channelTemplates;
  }

  @Override
  public int getCleanInterval() {
    Integer a_result =
      getConfiguration().getParameterValue(CLEAN_INTERVAL, Integer.class);
    return a_result == null ? 5 : a_result;
  }

  @Override
  @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
  public ManagerConfig getConfiguration() {
    return configuration;
  }

  @Override
  public int getMaxStorageTime() {
    Integer a_result =
      getConfiguration().getParameterValue(MAX_STORAGE_TIME, Integer.class);
    return a_result == null ? 24 : a_result;
  }

  @Override
  public String getVersion() {
    if (version == null) {
      try {
        version = HydraHelper.getVersion(this.getClass());
      }
      catch (Exception p_ex) {
        log.logError("", "", p_ex.getMessage(), null);
      }
    }
    return version;
  }

  public void init() {
    try {
      doInit();
    }
    catch (Exception p_ex) {
      log.logError(getId(), "", p_ex.getMessage(), p_ex);
    }
  }

  public void logError(String p_channelId, Throwable p_ex) {
    log.logError(getId(), p_channelId, p_ex.getMessage(), p_ex);
    ChannelConfig a_config;
    try {
      a_config = findChannel(p_channelId);
      ChannelStateInfo a_info = a_config.getChannelStateInfo();
      if (a_info != null) {
        a_info.setErrorMessage(p_ex.getMessage());
      }
    }
    catch (ChannelNotFoundException p_ex1) {
      log.logError(getId(), p_channelId, p_ex1.getMessage(), p_ex1);
    }
  }

  @Override
  @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
  public void removeChannel(ChannelConfig p_channelConfig) {
    if (p_channelConfig != null) {
      stopChannel(p_channelConfig);
      configuration.getChannels().remove(p_channelConfig);
      saveConfig();
      log.logInfo(getId(), p_channelConfig.getId(),
        getMessage(MSG_CHANNEL_REMOVED));
    }
  }

  @Override
  @AccessTimeout(unit = TimeUnit.MINUTES, value = 1)
  public void removeChannel(String p_channelId) throws ChannelNotFoundException {
    removeChannel(findChannel(p_channelId));
  }

  public void shutDown() {
    try {
      doShutDown();
    }
    catch (Exception p_ex) {
      log.logError(getId(), "", p_ex.getMessage(), p_ex);
    }
  }

  protected void doInit() throws Exception {
    log = new HydraLogger(LOGGER_PACKAGE + getId().toUpperCase());
    loadConfig();
    loadChannelTemplates();
    loadRouteTemplates();
    log.logInfo(getId(), "", getMessage(MSG_CONFIG_LOADED));
    initCamelContext();
    List<ChannelConfig> a_channels = configuration.getChannels();
    Collections.sort(a_channels, new ChannelStartupComparator());
    for (ChannelConfig a_channelConfig : a_channels) {
      try {
        a_channelConfig.setOwner(configuration);
        startChannel(a_channelConfig);
      }
      catch (Exception p_ex) {
        log.logError(getId(), a_channelConfig.getId(),
          getMessage(ERR_START_CHANNEL), p_ex);
      }
    }
    InitialContext a_context = new InitialContext();
    a_context.bind(HYDRA_JNDI + getId(), this);
    // a_context.bind(getId() + DLC_SUFFIX, new ChannelDeadLetterHandler());
    sendMessage(getId(), true);
    initStorageCleanRoute();
  }

  protected void doShutDown() throws Exception {
    camelContext.shutdownRoute(getStorageCleanRouteId());
    camelContext.removeRoute(getStorageCleanRouteId());
    camelContext.shutdownRoute(SUPERVISOR_ROUTE + getId());
    camelContext.removeRoute(SUPERVISOR_ROUTE + getId());
    List<ChannelConfig> a_channels = configuration.getChannels();
    Collections.sort(a_channels, new ChannelStartupComparator());
    Collections.reverse(a_channels);
    for (ChannelConfig a_channel : a_channels) {
      a_channel.setOwner(null);
      stopChannel(a_channel);
      // ChannelStateInfo a_stateInfo = a_channel.getChannelStateInfo();
      // if (a_stateInfo != null)
      // {
      // a_stateInfo.getRoutesId().clear();
      // a_channel.setChannelStateInfo(null);
      // }
    }
    saveConfig();
    // camelContext.stop();
    log.logInfo(getId(), "", getMessage(MSG_CONFIG_UNLOADED));
    try {
      InitialContext a_context = new InitialContext();
      a_context.unbind(HYDRA_JNDI + getId());
      sendMessage(getId(), false);
    }
    catch (Exception p_ex) {
      log.logWarn(getId(), "", getMessage(WARN_JNDI_UNBIND));
    }
  }

  protected String[] getCamelComponentNames() {
    return new String[0];
  }

  protected String getStorageJndiName() {
    return null;
  }

  protected void initStorageCleanRoute() throws Exception {
    String a_storageJndiName = getStorageJndiName();
    if (a_storageJndiName != null) {
      camelContext.addRoutes(new StorageCleanRouteBuilder());
    }
  }

  protected void registerBeans() throws Exception {

  }

  protected void resolveCamelComponents() throws Exception {
    String[] a_camelComponentsNames = getCamelComponentNames();
    if (a_camelComponentsNames != null && a_camelComponentsNames.length > 0) {
      ComponentResolver a_resolver = camelContext.getComponentResolver();
      for (int i = 0; i < a_camelComponentsNames.length; i++) {
        Component a_component =
          a_resolver
            .resolveComponent(a_camelComponentsNames[i], camelContext);
        if (a_component == null) {
          log.logWarn(getId(), "", "Can't resolve component with schema: "
            + a_camelComponentsNames[i]);
        }
      }
    }
  }

//	private int getRouteShutdownTimeout()
//	{
//		if (configuration != null)
//		{
//			ConfigParameter a_routeShutdownTimeout =
//					configuration.getParameter("routeShutdownTimeout");
//			if (a_routeShutdownTimeout == null)
//			{
//				a_routeShutdownTimeout =
//						new ConfigParameter("routeShutdownTimeout",
//								getMessage(LBL_ROUTE_SD_TO_NAME), 30,
//								getMessage(LBL_ROUTE_SD_TO_UOM),
//								getMessage(MessageKey.LBL_ROUTE_SD_TO_DESC));
//				configuration.getParameters().add(a_routeShutdownTimeout);
//			}
//			return a_routeShutdownTimeout.convertValue(Integer.class);
//		}
//		return 30;
//	}

  private String getStorageCleanRouteId() {
    return getId() + ".cleaner";
  }

  private void initCamelContext() throws Exception {
    // camelContext = new DefaultCamelContext();
    camelContext = (DefaultCamelContext) CamelContextHandler.getContext();
    // camelContext.setName(getId() + CONTEXT_SUFFIX);
    // camelContext.setErrorHandlerBuilder(new DeadLetterChannelBuilder("bean:"
    // + getId() + DLC_SUFFIX));
    resolveCamelComponents();
    registerBeans();
    camelContext.addRoutes(new ChannelsSupervisorRouteBuilder());
    // camelContext.start();
  }

  private void loadChannelTemplates() throws IOException, JAXBException {
    channelTemplates = new ArrayList<ChannelConfig>();
    String a_configRes = format(PATH_TO_RESOURCES + CONFIG_FILE_NAME, getId());
    String a_confStr =
      new String(
        HydraHelper.readContextFromResources(getClass(), a_configRes));
    ManagerConfig a_config = unmarshal(ManagerConfig.class, a_confStr);
    channelTemplates = a_config.getChannels();
    if (configuration == null) {
      configuration = a_config;
      configuration.setChannels(new ArrayList<ChannelConfig>());
      saveConfig();
    }
  }

  // private List<RouteDefinition> loadRoutes(String p_routes)
  // throws JAXBException
  // {
  // List<RouteDefinition> a_result = null;
  // if (p_routes != null)
  // {
  // StringReader a_reader = new StringReader(p_routes);
  // JAXBContext a_jaxbContext =
  // JAXBContext.newInstance(Constants.JAXB_CONTEXT_PACKAGES,
  // RoutesDefinition.class.getClassLoader());
  // Unmarshaller a_unmarshaller = a_jaxbContext.createUnmarshaller();
  // Object a_object = a_unmarshaller.unmarshal(a_reader);
  // RoutesDefinition a_routesDefinition = (RoutesDefinition) a_object;
  // a_result = a_routesDefinition.getRoutes();
  // }
  // return a_result == null ? new ArrayList<RouteDefinition>() : a_result;
  // }

  private void loadConfig() throws Exception {
    configuration =
      HydraHelper.loadConfig(getHydraConfigDir(), getId() + XML_EXT,
        ManagerConfig.class);
//		getRouteShutdownTimeout();
    // String a_configDir = getHydraConfigDir();
    // File a_configFolder = new File(a_configDir);
    // File a_configFile = new File(a_configFolder, getId() + XML_EXT);
    // if (!a_configFolder.exists())
    // {
    // a_configFolder.mkdirs();
    // }
    // if (a_configFile.exists())
    // {
    // configuration =
    // (ManagerConfig) unmarshal(ManagerConfig.class, a_configFile);
    // }
  }

  // private void marshal(Object p_instance, File p_file) throws JAXBException
  // {
  // JAXBContext a_context = JAXBContext.newInstance(p_instance.getClass());
  // Marshaller a_marshaller = a_context.createMarshaller();
  // a_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
  // a_marshaller.marshal(p_instance, p_file);
  // }
  //
  // private byte[] readContextFromResources(String p_resPath) throws
  // IOException
  // {
  // InputStream a_is = getClass().getResourceAsStream(p_resPath);
  // try
  // {
  // byte[] a_result = new byte[a_is.available()];
  // a_is.read(a_result);
  // return a_result;
  // }
  // finally
  // {
  // a_is.close();
  // }
  // }

  private void loadRouteTemplates() throws IOException {
    routeTemplates = new HashMap<String, String>();
    String a_resFolder = format(PATH_TO_ROUTES, getId());
    for (ChannelConfig a_channelConfig : channelTemplates) {
      String a_name = a_channelConfig.getResourceName();
      byte[] a_context =
        readContextFromResources(getClass(),
          format(PATH_TO_RESOURCE, a_resFolder, a_name));
      String a_routeTemplate = new String(a_context);
      routeTemplates.put(a_name, a_routeTemplate);
    }
  }

  private String resolveTemplate(String p_routesTemplate,
    ChannelConfig p_channelConfig) {
    Properties a_properties = p_channelConfig.getParametersAsProperties();
    String a_result = p_routesTemplate;
    Enumeration<?> a_names = a_properties.propertyNames();
    while (a_names.hasMoreElements()) {
      String a_name = (String) a_names.nextElement();
      StringBuilder a_stringBuilder = new StringBuilder();
      a_stringBuilder.append("$${");
      a_stringBuilder.append(a_name);
      a_stringBuilder.append("}");
      String a_pattern = Pattern.quote(a_stringBuilder.toString());
      a_result =
        a_result.replaceAll(a_pattern, a_properties.getProperty(a_name));
    }
    a_result =
      a_result.replaceAll(Pattern.quote("$${channel_id}"),
        p_channelConfig.getId());
    a_result =
      a_result.replaceAll(Pattern.quote(ROUTE_ID_PREFIX), ROUTE_ID_PREFIX
        + p_channelConfig.getId() + ROUTE_ID_DELIMITER);
    return a_result;

    // String[] a_routeParams = p_routesTemplate.split(PARAM_PATTERN);
    // StringBuilder a_builder = new StringBuilder();
    // Properties a_values = p_channelConfig.getParametersAsProperties();
    // String a_routeId = ROUTE_ID_PREFIX;
    // for (int i = 0; i < a_routeParams.length; i++)
    // {
    // if (InteractionManager.CHANNEL_ID.equals(a_routeParams[i]))
    // {
    // a_builder.append(p_channelConfig.getId());
    // }
    // else
    // {
    // String a_value = a_values.getProperty(a_routeParams[i]);
    // if (a_value != null)
    // {
    // a_builder.append(a_value);
    // }
    // else
    // {
    // String a_item =
    // a_routeParams[i].replaceAll(a_routeId, a_routeId
    // + p_channelConfig.getId() + ROUTE_ID_DELIMITER);
    // a_builder.append(a_item);
    // }
    // }
    // }
    // return a_builder.toString();
  }

  private void saveConfig() {
    try {
      HydraHelper.saveConfig(configuration, getHydraConfigDir(), getId()
        + XML_EXT);
      // marshal(configuration, a_configFile);
    }
    catch (JAXBException p_ex) {
      log.logError(getId(), "", getMessage(ERR_CONFIG_SAVE), p_ex);
    }
  }

  // private void restartChannel(ChannelConfig p_channelConfig)
  // {
  // log.logInfo(getId(), p_channelConfig.getId(),
  // getMessage(MSG_CHANNEL_RESTART));
  // stopChannel(p_channelConfig);
  // startChannel(p_channelConfig);
  // }

  private void sendMessage(String p_jndiName, boolean p_active) {
    Connection a_connection = null;
    try {
      InitialContext a_context = new InitialContext();
      ConnectionFactory a_factory =
        (ConnectionFactory) a_context.lookup("java:/ConnectionFactory");
      if (a_factory != null) {
        Destination a_destination =
          (Destination) a_context.lookup("queue/hydraManagerQueue");
        if (a_destination != null) {
          a_connection = a_factory.createConnection();
          Session a_session =
            a_connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
          MessageProducer a_producer = a_session.createProducer(a_destination);
          a_connection.start();
          Message a_message =
            a_session.createObjectMessage(new ManagerDeploymentEvent(
              p_jndiName, p_active));
          a_producer.send(a_message);
          a_connection.stop();
        }
      }
    }
    catch (IllegalArgumentException p_ex) {
      log.logWarn(getId(), "", p_ex.getMessage());
    }
    catch (NameNotFoundException p_ex) {
      log.logWarn(getId(), "", p_ex.getMessage());
    }
    catch (Exception p_ex) {
      log.logError(getId(), "", p_ex.getMessage(), p_ex);
    }
    finally {
      if (a_connection != null) {
        try {
          a_connection.close();
        }
        catch (JMSException p_ex) {
          log.logError(getId(), "", p_ex.getMessage(), p_ex);
        }
      }
    }

  }

  private void shutdownRoutes(String p_channelId, List<String> p_routesId,
    long p_shutdownTimeout) {
    for (String a_id : p_routesId) {
      try {
//				camelContext.stopRoute(a_id, p_shutdownTimeout, TimeUnit.MILLISECONDS);
        camelContext.shutdownRoute(a_id, p_shutdownTimeout, TimeUnit.MILLISECONDS);
        camelContext.removeRoute(a_id);
      }
      catch (Exception p_ex) {
        log.logError(getId(), p_channelId, getMessage(ERR_STOP_CHANNEL) + a_id,
          p_ex);
      }
    }
  }

  private void startChannel(ChannelConfig p_channelConfig) {
    if (p_channelConfig.isEnabled()
      && p_channelConfig.getChannelStateInfo() == null) {
      try {
        String a_routesTemplate =
          routeTemplates.get(p_channelConfig.getResourceName());
        if (a_routesTemplate != null) {
          String a_routes = resolveTemplate(a_routesTemplate, p_channelConfig);
          List<RouteDefinition> a_definitions = loadRoutes(a_routes);
          ChannelStateInfo a_channelStateInfo = new ChannelStateInfo();
//					ProcessDefinition a_processDefinition =
//							new ProcessDefinition(new StopChannelInterceptor());
//					InterceptDefinition a_interceptor = new InterceptDefinition();
//					a_interceptor.addOutput(a_processDefinition);
          BeanDefinition a_beginHandler = new BeanDefinition();
          a_beginHandler.setBean(a_channelStateInfo);
          a_beginHandler.setMethod("messageReceiving");
          BeanDefinition a_finishHandler = new BeanDefinition();
          a_finishHandler.setBean(a_channelStateInfo);
          a_finishHandler.setMethod("messageReceived");
          for (int i = 0; i < a_definitions.size(); i++) {
            RouteDefinition a_routeDefinition = a_definitions.get(i);
            a_channelStateInfo.getRoutesId().add(a_routeDefinition.getId());
            if (i == 0) {
              List<ProcessorDefinition<?>> a_outputs =
                a_routeDefinition.getOutputs();
              a_outputs.add(0, a_beginHandler);
//							a_outputs.add(0, a_interceptor);
              a_outputs.add(a_finishHandler);
              a_routeDefinition.setOutputs(a_outputs);
            }
          }
          p_channelConfig.setLastConnectTime(System.currentTimeMillis());
          p_channelConfig.setReconnectAttempts(p_channelConfig
            .getReconnectAttempts() + 1);
          camelContext.addRouteDefinitions(a_definitions);
          p_channelConfig.setChannelStateInfo(a_channelStateInfo);
          log.logInfo(getId(), p_channelConfig.getId(),
            getMessage(MSG_CHANNEL_STARTED));
        }
      }
      catch (Exception p_ex) {
        log.logError(getId(), p_channelConfig.getId(),
          getMessage(ERR_START_CHANNEL) + ": " + p_ex.getMessage(), p_ex);
      }
    }
  }

  private void stopChannel(ChannelConfig p_channelConfig) {
    if (p_channelConfig.getChannelStateInfo() != null) {
      ChannelStateInfo a_stateInfo = p_channelConfig.getChannelStateInfo();
      if (a_stateInfo != null) {
        p_channelConfig.setReconnectAttempts(p_channelConfig
          .getReconnectAttempts() - 11);
        shutdownRoutes(p_channelConfig.getId(), a_stateInfo.getRoutesId(),
          p_channelConfig.getShutdownTimeout());
        a_stateInfo.getRoutesId().clear();
        p_channelConfig.setChannelStateInfo(null);
      }
      // p_channelConfig.setInWork(false);
      log.logInfo(getId(), p_channelConfig.getId(),
        getMessage(MSG_CHANNEL_STOPPED));
    }
  }

  // @SuppressWarnings("unchecked")
  // private <T> T unmarshal(Class<T> p_class, String p_templateString)
  // throws JAXBException
  // {
  // StringReader a_reader = new StringReader(p_templateString);
  // JAXBContext a_jaxbContext = JAXBContext.newInstance(p_class);
  // Unmarshaller a_unmarshaller = a_jaxbContext.createUnmarshaller();
  // Object a_object = a_unmarshaller.unmarshal(a_reader);
  // return (T) a_object;
  // }

  private void stopRoutes(String p_channelId, List<String> p_routesId) {
    for (String a_id : p_routesId) {
      try {
        camelContext.stopRoute(a_id);
      }
      catch (Exception p_ex) {
        log.logError(getId(), p_channelId, getMessage(ERR_STOP_CHANNEL) + a_id,
          p_ex);
      }
    }
  }

  public class ChannelDeadLetterHandler implements Processor {
    @Override
    public void process(Exchange p_exchange) throws Exception {
      String a_channelId = (String) p_exchange.getIn().getHeader("channel_id");
      Throwable a_throwable =
        p_exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
      logError(a_channelId, a_throwable);
    }

  }

  public final class ChannelsSupervisor implements Processor {

    @Override
    public void process(Exchange p_exchange) throws Exception {
      if (configuration != null) {
        List<ChannelConfig> a_configs = configuration.getChannels();
        if (a_configs != null) {
          for (ChannelConfig a_channelConfig : a_configs) {
            checkChannelState(a_channelConfig);
          }
        }
      }
    }

    private void checkChannelState(ChannelConfig p_channelConfig) {
      if (p_channelConfig.isEnabled()) {
        ChannelStateInfo a_stateInfo = p_channelConfig.getChannelStateInfo();
        if (a_stateInfo == null) {
          reconnect(p_channelConfig);
        }
        else {
          checkExperiod(p_channelConfig, a_stateInfo);
        }
      }
    }

    private void checkExperiod(ChannelConfig p_channelConfig,
      ChannelStateInfo p_stateInfo) {
      long a_maxMessageTimeout = p_channelConfig.getMaxMessageTimeout();
      if (a_maxMessageTimeout > 0) {
        if (System.currentTimeMillis() - p_stateInfo.getMessageTime() >
          a_maxMessageTimeout) {
          log.logWarn(getId(), p_channelConfig.getId(),
            Messages.getMessage(MessageKey.WARN_MSG_TIMEOUT));
          restartRoutes(p_channelConfig);
        }

      }
    }

    private void reconnect(ChannelConfig p_channelConfig) {
      int a_maxCount = p_channelConfig.getReconnectCount();
      if (a_maxCount >= 0) {
        if (a_maxCount > 0
          && p_channelConfig.getReconnectAttempts() > a_maxCount) {
          return;
        }
        long a_currentTime = System.currentTimeMillis();
        long a_lastTime = p_channelConfig.getLastConnectTime();
        long a_interval = p_channelConfig.getReconnectTimeout();
        if (a_currentTime - a_lastTime >= a_interval) {
          startChannel(p_channelConfig);
        }
      }
    }

    private void restartRoutes(ChannelConfig p_channelConfig) {
      if (p_channelConfig.getChannelStateInfo() != null) {
        ChannelStateInfo a_stateInfo = p_channelConfig.getChannelStateInfo();
        if (a_stateInfo != null) {
          p_channelConfig.setReconnectAttempts(p_channelConfig
            .getReconnectAttempts() - 11);
          List<String> a_routeIds = a_stateInfo.getRoutesId();
          stopRoutes(p_channelConfig.getId(), a_routeIds);
          try {
            for (String a_id : a_routeIds) {
              camelContext.startRoute(a_id);
            }
            a_stateInfo.fireRestart();
          }
          catch (Exception p_ex) {
            log.logError(getId(), p_channelConfig.getId(), p_ex.getMessage(),
              p_ex);
            stopRoutes(p_channelConfig.getId(), a_routeIds);
          }
        }
      }
    }
  }

  public final class ChannelsSupervisorRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
      from(
        "timer://timer-" + getId()
          + "?delay=30000&fixedRate=true&period=30000").routeId(
        SUPERVISOR_ROUTE + getId()).process(new ChannelsSupervisor());
    }
  }

  private final class StorageCleanRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
      from(
        "timer://" + getId() + "CleanTimer?period=1m&delay=1m&fixedRate=true")
        .routeId(getStorageCleanRouteId()).setBody(header(Exchange.TIMER_FIRED_TIME))
        .inOnly(getStorageJndiName() + "?method=clean");
//        .log(LoggingLevel.DEBUG,        "Outdated data removed");
    }

  }

}
