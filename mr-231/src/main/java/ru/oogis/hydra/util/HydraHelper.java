package ru.oogis.hydra.util;

import org.apache.camel.model.Constants;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import ru.oogis.hydra.config.ConfigElement;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HydraHelper {
  static final String DESCRIPTION = ".description";
  static final String DISPLAY_NAME = ".displayName";
  private static final String AS_CONF_DIR_KEY = "jboss.server.config.dir";

  // private static HydraConfig hydraConfig;

  public static <T> T castObject(Object p_value, Class<T> p_class)
    throws Exception {
    if (p_value != null && p_class != null) {
      return p_class.cast(p_value);
    }
    else {
      throw new IllegalArgumentException();
    }
  }

  public static void checkFolder(File p_folder, boolean p_create)
    throws IOException {
    if (!p_folder.exists()) {
      if (!p_create) {
        throw new FileNotFoundException(p_folder.getAbsolutePath());
      }
      p_folder.mkdirs();
    }
    if (!p_folder.isDirectory()) {
      throw new IOException(p_folder.getAbsolutePath() + " is not a folder");
    }
  }


  // public static HydraConfig getHydraConfig()
  // {
  // hydraConfig =
  // loadConfig(getHydraConfigDir(), HYDRA_CONFIG_FILE,
  // HydraConfig.class);
  // }
  // return hydraConfig;
  // }

  public static String getHydraConfigDir() {
    String a_confPath = System.getProperty(AS_CONF_DIR_KEY);
    if (a_confPath == null) {
      a_confPath = ".";
    }
    return a_confPath + "/hydra";
  }

  public static String getVersion(Class<?> p_class) throws IOException {
    InputStream a_stream = p_class.getResourceAsStream("/version.properties");
    String a_version = "";
    if (a_stream != null) {
      Properties a_properties = new Properties();
      try {
        a_properties.load(a_stream);
        a_version = a_properties.getProperty("version", "");
      }
      finally {
        a_stream.close();
      }
    }
    return a_version;

  }

  public static boolean isLater(Timestamp p_newTime, Timestamp p_oldTime) {
    if (p_newTime == null) {
      return false;
    }
    if (p_oldTime == null) {
      return true;
    }
    return p_newTime.after(p_oldTime);
  }

  public static <T extends ConfigElement> T loadConfig(String p_configDir,
    String p_configFile, Class<T> p_class) throws Exception {

    File a_configFolder = new File(p_configDir);
    File a_configFile = new File(a_configFolder, p_configFile);
    if (a_configFile.exists()) {
      return (T) unmarshal(p_class, a_configFile);
    }
    // T a_result = p_class.newInstance();
    // saveConfig(a_result, p_configDir, p_configFile);
    // return a_result;
    return null;
  }

  public static synchronized List<RouteDefinition> loadRoutes(String p_routes)
    throws JAXBException {
    List<RouteDefinition> a_result = null;
    if (p_routes != null) {
      StringReader a_reader = new StringReader(p_routes);
      JAXBContext a_jaxbContext =
        JAXBContext.newInstance(Constants.JAXB_CONTEXT_PACKAGES,
          RoutesDefinition.class.getClassLoader());
      Unmarshaller a_unmarshaller = a_jaxbContext.createUnmarshaller();
      Object a_object = a_unmarshaller.unmarshal(a_reader);
      RoutesDefinition a_routesDefinition = (RoutesDefinition) a_object;
      a_result = a_routesDefinition.getRoutes();
    }
    return a_result == null ? new ArrayList<RouteDefinition>() : a_result;
  }

  public static synchronized byte[] readContextFromResources(Class<?> p_class,
    String p_resPath) throws IOException {
    if (p_class != null && p_resPath != null) {
      InputStream a_is = p_class.getResourceAsStream(p_resPath);
      try {
        byte[] a_result = new byte[a_is.available()];
        a_is.read(a_result);
        return a_result;
      }
      finally {
        a_is.close();
      }
    }
    return null;
  }

  public static <T extends ConfigElement> void saveConfig(T p_config,
    String p_configDir, String p_configFile) throws JAXBException {
    if (p_config != null) {
      File a_configFolder = new File(p_configDir);
      File a_configFile = new File(a_configFolder, p_configFile);
      if (!a_configFolder.exists()) {
        a_configFolder.mkdirs();
      }
      marshal(p_config, a_configFile);
    }
  }

  @SuppressWarnings("unchecked")
  public static synchronized <T> T unmarshal(Class<T> p_class,
    String p_templateString) throws JAXBException {
    StringReader a_reader = new StringReader(p_templateString);
    JAXBContext a_jaxbContext = JAXBContext.newInstance(p_class);
    Unmarshaller a_unmarshaller = a_jaxbContext.createUnmarshaller();
    Object a_object = a_unmarshaller.unmarshal(a_reader);
    return (T) a_object;
  }

  // public static void saveHydraConfig() throws JAXBException
  // {
  // saveConfig(hydraConfig, getHydraConfigDir(), HYDRA_CONFIG_FILE);
  // }

  private static <T extends ConfigElement> void marshal(T p_instance,
    File p_file) throws JAXBException {
    JAXBContext a_context = JAXBContext.newInstance(p_instance.getClass());
    Marshaller a_marshaller = a_context.createMarshaller();
    a_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    a_marshaller.marshal(p_instance, p_file);
  }

  @SuppressWarnings("unchecked")
  private static <T extends ConfigElement> T unmarshal(Class<T> p_class,
    File p_file) throws JAXBException {
    JAXBContext a_context = JAXBContext.newInstance(p_class);
    Unmarshaller a_unmarshaller = a_context.createUnmarshaller();
    return (T) a_unmarshaller.unmarshal(p_file);
  }
}
